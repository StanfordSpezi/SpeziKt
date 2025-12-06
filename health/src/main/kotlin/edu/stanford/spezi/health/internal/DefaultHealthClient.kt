package edu.stanford.spezi.health.internal

import androidx.fragment.app.FragmentActivity
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.changes.DeletionChange
import androidx.health.connect.client.changes.UpsertionChange
import androidx.health.connect.client.records.Record
import androidx.health.connect.client.records.metadata.DataOrigin
import androidx.health.connect.client.request.ChangesTokenRequest
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.response.ChangesResponse
import androidx.health.connect.client.time.TimeRangeFilter
import edu.stanford.spezi.core.coroutines.Concurrency
import edu.stanford.spezi.health.AnyRecordType
import edu.stanford.spezi.health.CollectionMode
import edu.stanford.spezi.health.Health
import edu.stanford.spezi.health.HealthConstraint
import edu.stanford.spezi.health.HealthQueryTimeRange
import edu.stanford.spezi.health.QueryResult
import edu.stanford.spezi.health.QuerySort
import edu.stanford.spezi.health.RecordType
import edu.stanford.spezi.health.healthLogger
import edu.stanford.spezi.storage.local.LocalStorage
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.time.Instant
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.time.Duration

/**
 * The default implementation of [HealthClient] using Health Connect.
 */
@Suppress("TooManyFunctions")
internal class DefaultHealthClient(
    private val configurations: Set<HealthConfigurationComponent>,
    private val standard: HealthConstraint?,
    concurrency: Concurrency,
    val healthConnectClient: HealthConnectClient,
    localStorage: LocalStorage,
) : HealthClient {

    private val mainScope = concurrency.mainImmediateCoroutineScope()
    val ioScope = concurrency.ioCoroutineScope()
    private val permissionController = healthConnectClient.permissionController
    override val dataAccessRequirements = configurations.fold(HealthDataAccessRequirements()) { acc, component ->
        acc + component.dataAccessRequirements
    }
    private val allRequiredPermissions = with(dataAccessRequirements) {
        read.map { it.readPermission } + write.map { it.writePermission }
    }.toSet()

    private val logger by healthLogger()
    private val registeredDataCollectors = CopyOnWriteArrayList<HealthDataCollector>()

    val changesTokenStore = ChangesTokenStore(storage = localStorage)

    override var configState: Health.ConfigState = Health.ConfigState.Pending
        private set

    private val _isFullyAuthorizedState = MutableStateFlow(false)
    override val isFullyAuthorizedState = _isFullyAuthorizedState.asStateFlow()

    override fun configure() {
        ioScope.launch {
            logger.i { "Started configuration of health client" }
            configState = Health.ConfigState.Ongoing
            updateIsFullyAuthorizedState()
            configurations.forEach {
                it.configure(client = this@DefaultHealthClient, standard = standard)
            }
            configState = Health.ConfigState.Completed
            logger.i { "Completed configuration of health client" }
        }
    }

    override fun onPermissionsGranted(granted: Set<String>) {
        logger.i { "Health permissions granted: $granted" }
        mainScope.launch {
            updateIsFullyAuthorizedState()
            registeredDataCollectors.forEach { collector ->
                if (granted.contains(collector.recordType.readPermission)) {
                    startAutomaticDataCollectionIfPossible(collector)
                }
            }
        }
    }

    override fun requestPermissionsIfNeeded(activity: FragmentActivity) {
        mainScope.launch {
            val neededPermissions = neededPermissions()
            if (neededPermissions.isNotEmpty()) {
                startPermissionFlow(activity = activity, permissions = neededPermissions)
            }
        }
    }

    override fun requestReadPermission(type: AnyRecordType, activity: FragmentActivity) {
        mainScope.launch {
            if (isAuthorizedToRead(type).not()) {
                startPermissionFlow(activity = activity, permissions = setOf(type.readPermission))
            }
        }
    }

    override fun requestWritePermission(type: AnyRecordType, activity: FragmentActivity) {
        mainScope.launch {
            if (isAuthorizedToWrite(type).not()) {
                startPermissionFlow(activity = activity, permissions = setOf(type.writePermission))
            }
        }
    }

    override fun resetRecordCollection(type: AnyRecordType) {
        ioScope.launch {
            val collectorsToReset = registeredDataCollectors.filter { it.recordType == type }
            changesTokenStore.deleteToken(type)
            collectorsToReset.forEach { collector ->
                collector.stopDataCollection()
            }
            registeredDataCollectors.removeAll(collectorsToReset)
        }
    }

    override suspend fun <T : Record> query(
        type: RecordType<T>,
        timeRange: HealthQueryTimeRange,
        source: String?,
        sortedBy: QuerySort,
        limit: Int,
        predicate: ((T) -> Boolean)?,
    ): List<T> {
        if (isAuthorizedToRead(type).not()) {
            logger.w { "Not authorized to read data of type: ${type.identifier}" }
            return emptyList()
        }
        val response = healthConnectClient.readRecords(
            ReadRecordsRequest(
                recordType = type.type,
                timeRangeFilter = TimeRangeFilter.between(timeRange.start, timeRange.end),
                dataOriginFilter = source?.let { setOf(DataOrigin(it)) } ?: emptySet(),
                pageSize = limit,
            )
        )

        val filtered = response.records.filter { predicate?.invoke(it) ?: true }
        return when (sortedBy) {
            QuerySort.NONE -> filtered
            QuerySort.BY_START_TIME_ASC -> filtered.sortedBy { it.startTime() }
            QuerySort.BY_START_TIME_DESC -> filtered.sortedByDescending { it.startTime() }
        }
    }

    override suspend fun <T : Record> query(
        type: RecordType<T>,
        timeRange: HealthQueryTimeRange,
        anchor: String?,
        predicate: ((T) -> Boolean)?,
    ): QueryResult<T> {
        if (!isAuthorizedToRead(type)) {
            logger.w { "Not authorized to read data for ${type.identifier}" }
            return QueryResult(added = emptyList(), deletedIds = emptyList(), nextAnchor = anchor)
        }
        val token = anchor ?: getChangesToken(type)
        val response = healthConnectClient.getChanges(token)

        if (response.changesTokenExpired) {
            val newToken = getChangesToken(type)
            val changes = healthConnectClient.getChanges(newToken)
            return createQueryResult(
                response = changes,
                type = type,
                timeRange = timeRange,
                predicate = predicate,
            )
        }

        return createQueryResult(
            response = response,
            type = type,
            timeRange = timeRange,
            predicate = predicate,
        )
    }

    override fun <T : Record> continuousQuery(
        type: RecordType<T>,
        timeRange: HealthQueryTimeRange,
        interval: Duration,
        anchor: String?,
        predicate: ((T) -> Boolean)?,
    ): Flow<QueryResult<T>> = flow {
        if (!isAuthorizedToRead(type)) {
            logger.w { "Not authorized to read ${type.identifier}" }
            return@flow
        }

        var currentAnchor = anchor ?: getChangesToken(type)

        while (currentCoroutineContext().isActive) {
            val result = query(
                type = type,
                timeRange = timeRange,
                anchor = currentAnchor,
                predicate = predicate,
            )

            if (result.added.isNotEmpty() || result.deletedIds.isNotEmpty()) {
                emit(
                    QueryResult(
                        added = result.added,
                        deletedIds = result.deletedIds,
                        nextAnchor = result.nextAnchor
                    )
                )
            }

            result.nextAnchor?.let { currentAnchor = it }
            delay(interval)
        }
    }

    override suspend fun <T : Record> oldestSampleDate(type: RecordType<T>): Instant? {
        if (!isAuthorizedToRead(type)) {
            logger.w { "Not authorized to read data for ${type.identifier}" }
            return null
        }

        val response = healthConnectClient.readRecords(
            request = ReadRecordsRequest(
                recordType = type.type,
                timeRangeFilter = TimeRangeFilter.after(Instant.MIN),
                pageSize = 1
            )
        )

        return response.records.firstOrNull()?.startTime()
    }

    override suspend fun isAuthorizedToRead(type: AnyRecordType): Boolean {
        return getGrantedPermissions().contains(type.readPermission)
    }

    override suspend fun isAuthorizedToWrite(type: AnyRecordType): Boolean {
        return getGrantedPermissions().contains(type.writePermission)
    }

    private suspend fun neededPermissions(): Set<String> {
        return (allRequiredPermissions - getGrantedPermissions()).toSet()
    }

    override suspend fun addHealthDataCollector(collector: HealthDataCollector) {
        val existingIndex = registeredDataCollectors.indexOfFirst { it.recordType == collector.recordType }
        val action: CollectorAction = if (existingIndex == -1) {
            CollectorAction.Add
        } else {
            val existing = registeredDataCollectors[existingIndex]
            val existingSetting = existing.deliverySetting
            val newSetting = collector.deliverySetting
            when {
                existingSetting == newSetting -> CollectorAction.Ignore
                existingSetting.continueInBackground && !newSetting.continueInBackground -> CollectorAction.Ignore
                !existingSetting.continueInBackground && newSetting.continueInBackground -> CollectorAction.Replace(existingIndex)
                else -> CollectorAction.Ignore
            }
        }

        when (action) {
            is CollectorAction.Add -> {
                registeredDataCollectors.add(collector)
                startAutomaticDataCollectionIfPossible(collector)
                logger.i { "Added HealthDataCollector for ${collector.recordType.identifier}" }
            }

            is CollectorAction.Replace -> {
                val existing = registeredDataCollectors.getOrNull(action.index)
                existing?.stopDataCollection()
                registeredDataCollectors[action.index] = collector
                startAutomaticDataCollectionIfPossible(collector)
                logger.i { "Replaced HealthDataCollector for ${collector.recordType.identifier}" }
            }

            is CollectorAction.Ignore -> {
                logger.i { "Ignored adding HealthDataCollector for ${collector.recordType.identifier}" }
            }
        }
    }

    override suspend fun triggerDataSourceCollection() {
        registeredDataCollectors.forEach { collector ->
            if (collector.deliverySetting.collectionMode is CollectionMode.Manual && isAuthorizedToRead(collector.recordType)) {
                collector.startDataCollection()
            }
        }
    }

    private suspend fun startAutomaticDataCollectionIfPossible(collector: HealthDataCollector) {
        if (collector.isActive.not() &&
            collector.deliverySetting.collectionMode is CollectionMode.Automatic &&
            isAuthorizedToRead(collector.recordType)
        ) {
            collector.startDataCollection()
            logger.i { "Started automatic data collection for ${collector.recordType.identifier}" }
        }
    }

    private fun startPermissionFlow(activity: FragmentActivity, permissions: Set<String>) {
        HealthPermissionFragment.startPermissionFlow(activity = activity, permissions = permissions)
    }

    private suspend fun updateIsFullyAuthorizedState() {
        _isFullyAuthorizedState.update { neededPermissions().isEmpty() }
    }

    private suspend fun getGrantedPermissions(): Set<String> = runCatching {
        permissionController.getGrantedPermissions()
    }.getOrElse {
        logger.e(it) { "Failed to get granted permissions" }
        emptySet()
    }

    private suspend fun getChangesToken(type: AnyRecordType): String {
        return healthConnectClient.getChangesToken(ChangesTokenRequest(setOf(type.type)))
    }

    private fun <T : Record> createQueryResult(
        response: ChangesResponse,
        type: RecordType<T>,
        timeRange: HealthQueryTimeRange,
        predicate: ((T) -> Boolean)?,
    ): QueryResult<T> {
        val added = mutableListOf<T>()
        val deleted = mutableListOf<String>()

        response.changes.forEach { change ->
            when (change) {
                is UpsertionChange -> {
                    val rec = change.record
                    if (type.type.isInstance(rec)) {
                        @Suppress("UNCHECKED_CAST")
                        val typed = rec as T
                        if (matchesAnchoredQueryFilter(typed, timeRange, predicate)) {
                            added += typed
                        }
                    }
                }

                is DeletionChange ->
                    deleted += change.recordId
            }
        }

        return QueryResult(
            added = added,
            deletedIds = deleted,
            nextAnchor = response.nextChangesToken
        )
    }

    private fun <T : Record> matchesAnchoredQueryFilter(
        record: T,
        timeRange: HealthQueryTimeRange,
        predicate: ((T) -> Boolean)?,
    ): Boolean {
        if (predicate?.invoke(record) == false) return false
        val start = record.startTime() ?: return true
        return start >= timeRange.start && start <= timeRange.end
    }

    private sealed interface CollectorAction {
        data object Add : CollectorAction
        data object Ignore : CollectorAction
        data class Replace(val index: Int) : CollectorAction
    }
}
