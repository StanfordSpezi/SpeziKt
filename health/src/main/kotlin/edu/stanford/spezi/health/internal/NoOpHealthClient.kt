package edu.stanford.spezi.health.internal

import androidx.fragment.app.FragmentActivity
import androidx.health.connect.client.records.Record
import edu.stanford.spezi.health.AnyRecordType
import edu.stanford.spezi.health.Health
import edu.stanford.spezi.health.HealthQueryTimeRange
import edu.stanford.spezi.health.QueryResult
import edu.stanford.spezi.health.QuerySort
import edu.stanford.spezi.health.RecordType
import edu.stanford.spezi.health.healthLogger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.emptyFlow
import java.time.Instant
import kotlin.time.Duration

internal class NoOpHealthClient : HealthClient {
    override val dataAccessRequirements: HealthDataAccessRequirements = HealthDataAccessRequirements()
    private val logger by healthLogger()
    override val configState: Health.ConfigState = Health.ConfigState.Completed
    private val _isFullyAuthorizedState = MutableStateFlow(false)
    override val isFullyAuthorizedState: StateFlow<Boolean> = _isFullyAuthorizedState.asStateFlow()

    override fun configure() {
        logger.e { "NoOpHealthClient configured. Note that all operations will be disabled" }
    }

    override fun onPermissionsGranted(granted: Set<String>) {
        logger.w { "NoOpHealthClient can not receive permission granted callbacks" }
    }

    override fun requestPermissionsIfNeeded(activity: FragmentActivity) {
        logger.w { "NoOpHealthClient can not process permission requests" }
    }

    override fun requestReadPermission(type: AnyRecordType, activity: FragmentActivity) {
        logger.w { "NoOpHealthClient can not process permission requests" }
    }

    override fun requestWritePermission(type: AnyRecordType, activity: FragmentActivity) {
        logger.w { "NoOpHealthClient can not process permission requests" }
    }

    override fun resetRecordCollection(type: AnyRecordType) {
        logger.w { "NoOpHealthClient can not reset record collections" }
    }

    override suspend fun <T : Record> query(
        type: RecordType<T>,
        timeRange: HealthQueryTimeRange,
        source: String?,
        sortedBy: QuerySort,
        limit: Int,
        predicate: ((T) -> Boolean)?,
    ): List<T> {
        logger.w { "NoOpHealthClient can not perform query requests" }
        return emptyList()
    }

    override suspend fun <T : Record> query(
        type: RecordType<T>,
        timeRange: HealthQueryTimeRange,
        anchor: String?,
        predicate: ((T) -> Boolean)?,
    ): QueryResult<T> {
        logger.w { "NoOpHealthClient can not perform query requests" }
        return QueryResult(added = emptyList(), deletedIds = emptyList(), nextAnchor = null)
    }

    override fun <T : Record> continuousQuery(
        type: RecordType<T>,
        timeRange: HealthQueryTimeRange,
        interval: Duration,
        anchor: String?,
        predicate: ((T) -> Boolean)?,
    ): Flow<QueryResult<T>> {
        logger.w { "NoOpHealthClient can not perform continuous query requests" }
        return emptyFlow()
    }

    override suspend fun <T : Record> oldestSampleDate(type: RecordType<T>): Instant? {
        logger.w { "NoOpHealthClient can not retrieve oldest sample dates" }
        return null
    }

    override suspend fun addHealthDataCollector(collector: HealthDataCollector) {
        logger.w { "NoOpHealthClient can not add HealthDataCollectors" }
    }

    override suspend fun triggerDataSourceCollection() {
        logger.w { "NoOpHealthClient can not trigger data source collection" }
    }

    override suspend fun isAuthorizedToRead(type: AnyRecordType): Boolean {
        return false
    }

    override suspend fun isAuthorizedToWrite(type: AnyRecordType): Boolean {
        return false
    }
}
