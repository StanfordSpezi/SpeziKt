package edu.stanford.spezi.health.internal

import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.changes.DeletionChange
import androidx.health.connect.client.changes.UpsertionChange
import androidx.health.connect.client.records.Record
import androidx.health.connect.client.request.ChangesTokenRequest
import androidx.health.connect.client.response.ChangesResponse
import edu.stanford.spezi.health.AnyRecordType
import edu.stanford.spezi.health.CollectionMode
import edu.stanford.spezi.health.CollectionTimeRange
import edu.stanford.spezi.health.HealthConstraint
import edu.stanford.spezi.health.healthLogger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration

/**
 * Component responsible for collecting Health data of a specific [recordType] based on the provided
 * [deliverySetting], [timeRange], and optional [predicate] filter.
 *
 * It utilizes a [ChangesTokenStore] to manage changes tokens for efficient data retrieval
 * and interacts with a [HealthConstraint] to deliver collected data.
 */
@Suppress("LongParameterList")
internal class HealthDataCollector(
    val recordType: AnyRecordType,
    val deliverySetting: HealthDataCollectorDeliverySetting,
    private val timeRange: CollectionTimeRange,
    private val predicate: ((Record) -> Boolean)?,
    private val tokenStore: ChangesTokenStore,
    private val scope: CoroutineScope,
    private val healthConstraint: HealthConstraint,
    private val client: HealthConnectClient,
) {
    private val logger by healthLogger()
    private var collectionJob: Job? = null

    val isActive: Boolean
        get() = collectionJob?.isActive == true

    fun startDataCollection() {
        if (isActive) return

        collectionJob = scope.launch {
            when (val mode = deliverySetting.collectionMode) {
                is CollectionMode.Manual -> {
                    val result = getChangesOrResync()
                    processResult(result)
                }

                is CollectionMode.Automatic -> {
                    runPollingLoop(mode.pollingInterval)
                }
            }
        }
    }

    fun stopDataCollection() {
        collectionJob?.cancel()
        collectionJob = null
    }

    private suspend fun getChangesOrResync(): ChangesResponse {
        var token = tokenStore.getToken(recordType)

        if (token == null) {
            token = client.getChangesToken(
                ChangesTokenRequest(setOf(recordType.type))
            )
            tokenStore.storeToken(recordType, token)
        }

        val response = client.getChanges(token)

        return if (response.changesTokenExpired) {
            handleTokenExpired()
        } else {
            tokenStore.storeToken(recordType, response.nextChangesToken)
            response
        }
    }

    private suspend fun handleTokenExpired(): ChangesResponse {
        logger.w { "Token expired for $recordType. Performing full resync." }
        healthConstraint.onFullyResyncRequired(recordType)
        tokenStore.deleteToken(recordType)
        val newToken = client.getChangesToken(ChangesTokenRequest(setOf(recordType.type)))
        tokenStore.storeToken(recordType, newToken)
        return client.getChanges(newToken)
    }

    private suspend fun runPollingLoop(interval: Duration) {
        while (isActive) {
            runCatching {
                val result = getChangesOrResync()
                processResult(result)
                if (!result.hasMore) delay(interval)
            }.onFailure {
                logger.e(it) { "Error collecting Health data for $recordType" }
                delay(interval)
            }
        }
    }

    private suspend fun processResult(result: ChangesResponse) {
        val inserts = mutableSetOf<Record>()
        val deletes = mutableSetOf<String>()

        for (change in result.changes) {
            when (change) {
                is UpsertionChange -> {
                    val record = change.record
                    if (matchesFilter(record)) inserts += record
                }

                is DeletionChange -> deletes += change.recordId
            }
        }

        if (inserts.isNotEmpty()) {
            healthConstraint.handleNewRecords(inserts.toSet(), recordType)
        }
        if (deletes.isNotEmpty()) {
            healthConstraint.handleDeletedRecords(deletes.toSet(), recordType)
        }
    }

    private fun matchesFilter(record: Record): Boolean {
        if (!recordType.type.java.isAssignableFrom(record::class.java)) return false
        val predicateFilterMatched = predicate?.invoke(record) ?: true
        if (!predicateFilterMatched) return false

        return when (timeRange) {
            CollectionTimeRange.NewRecords -> true
            is CollectionTimeRange.StartingAt -> {
                val startInstant = record.startTime()
                val minDate = timeRange.date
                startInstant == null || startInstant >= minDate
            }
        }
    }
}
