package edu.stanford.spezi.health.internal

import androidx.fragment.app.FragmentActivity
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.records.Record
import edu.stanford.spezi.core.ApplicationModule
import edu.stanford.spezi.core.coroutines.Concurrency
import edu.stanford.spezi.core.lifecycle.AppLifecycle
import edu.stanford.spezi.health.AnyRecordType
import edu.stanford.spezi.health.Health
import edu.stanford.spezi.health.HealthConstraint
import edu.stanford.spezi.health.HealthDataAccessRequirements
import edu.stanford.spezi.health.HealthQueryTimeRange
import edu.stanford.spezi.health.QueryResult
import edu.stanford.spezi.health.QuerySort
import edu.stanford.spezi.health.RecordType
import edu.stanford.spezi.storage.local.LocalStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import java.time.Instant
import kotlin.time.Duration

@Suppress("TooManyFunctions")
internal interface HealthClient {
    /**
     * The current configuration state of the HealthClient.
     */
    val configState: Health.ConfigState

    /**
     * A [StateFlow] indicating whether all requested permissions have been granted.
     */
    val isFullyAuthorizedState: StateFlow<Boolean>

    /**
     * The [edu.stanford.spezi.health.HealthDataAccessRequirements] defining the requested read and write access.
     */
    val dataAccessRequirements: HealthDataAccessRequirements

    /**
     * Configures the HealthClient with the provided [HealthConfigurationComponent]s.
     */
    fun configure()

    /**
     * Notifies the HealthClient that the specified permissions have been granted.
     *
     * @param granted The set of granted permission strings.
     */
    fun onPermissionsGranted(granted: Set<String>)

    /**
     * Requests all permissions that have not yet been granted.
     *
     * @param activity The [FragmentActivity] to use for requesting permissions.
     */
    fun requestPermissionsIfNeeded(activity: FragmentActivity)

    /**
     * Requests read permission for the specified [AnyRecordType].
     *
     * @param type The [AnyRecordType] to request read permission for.
     * @param activity The [FragmentActivity] to use for requesting permission.
     */
    fun requestReadPermission(type: AnyRecordType, activity: FragmentActivity)

    /**
     * Requests write permission for the specified [AnyRecordType].
     *
     * @param type The [AnyRecordType] to request write permission for.
     * @param activity The [FragmentActivity] to use for requesting permission.
     */
    fun requestWritePermission(type: AnyRecordType, activity: FragmentActivity)

    /**
     * Checks if the client is authorized to read data of the specified [AnyRecordType].
     *
     * @param type The [AnyRecordType] to check read authorization for.
     * @return `true` if authorized to read, `false` otherwise.
     */
    suspend fun isAuthorizedToRead(type: AnyRecordType): Boolean

    /**
     * Checks if the client is authorized to write data of the specified [AnyRecordType].
     *
     * @param type The [AnyRecordType] to check write authorization for.
     * @return `true` if authorized to write, `false` otherwise.
     */
    suspend fun isAuthorizedToWrite(type: AnyRecordType): Boolean

    /**
     * Resets the internal state associated with a record type's collection via collect record.
     *
     * Use this function to reset the collection state, so that the next time a collector is registered,
     * its behavior will be as if it were being registered for the first time.
     */
    fun resetRecordCollection(type: AnyRecordType)

    /**
     * Adds a [HealthDataCollector] to the client for data collection.
     *
     * @param collector The [HealthDataCollector] to add.
     */
    suspend fun addHealthDataCollector(collector: HealthDataCollector)

    /**
     * Triggers an immediate collection of data from all registered data collectors with a manual collection mode.
     */
    suspend fun triggerDataSourceCollection()

    /**
     * Inserts a [Record] into Health Connect.
     *
     * @param record The [Record] to insert.
     * @return `true` if the insertion was successful, `false` otherwise.
     */
    suspend fun insert(record: Record): Boolean

    /**
     * Queries health data of the given [Record] [type].
     *
     * @param type The [RecordType] to query data for.
     * @param timeRange The [HealthQueryTimeRange] defining the time range for the query.
     * @param source An optional source filter for the query.
     * @param sortedBy The [QuerySort] defining the sorting order of the results.
     * @param limit The maximum number of records to return.
     * @param predicate An optional predicate to filter the results.
     *
     * @return A list of queried records of type [T].
     */
    suspend fun <T : Record> query(
        type: RecordType<T>,
        timeRange: HealthQueryTimeRange = HealthQueryTimeRange.today(),
        source: String? = null,
        sortedBy: QuerySort = QuerySort.NONE,
        limit: Int = Int.MAX_VALUE,
        predicate: ((T) -> Boolean)? = null,
    ): List<T>

    /**
     * Queries health data of the given [Record] [type] with pagination support.
     *
     * @param type The [RecordType] to query data for.
     * @param timeRange The [HealthQueryTimeRange] defining the time range for the query.
     * @param anchor An optional anchor string for pagination.
     * @param predicate An optional predicate to filter the results.
     *
     * @return A [QueryResult] containing the queried records, deleted IDs, and next anchor.
     */
    suspend fun <T : Record> query(
        type: RecordType<T>,
        timeRange: HealthQueryTimeRange,
        anchor: String?,
        predicate: ((T) -> Boolean)?,
    ): QueryResult<T>

    /**
     * Performs a continuous query for health data of the given [Record] [type].
     *
     * @param type The [RecordType] to query data for.
     * @param timeRange The [HealthQueryTimeRange] defining the time range for the query.
     * @param interval The [Duration] defining the interval between queries.
     * @param anchor An optional anchor string for pagination.
     * @param predicate An optional predicate to filter the results.
     *
     * @return A [Flow] emitting [QueryResult]s containing the queried records, deleted IDs, and next anchor.
     */
    fun <T : Record> continuousQuery(
        type: RecordType<T>,
        timeRange: HealthQueryTimeRange,
        interval: Duration,
        anchor: String?,
        predicate: ((T) -> Boolean)?,
    ): Flow<QueryResult<T>>

    /**
     * Retrieves the oldest sample date for the given [Record] [type].
     *
     * @param type The [RecordType] to retrieve the oldest sample date for.
     *
     * @return The oldest sample date as an [Instant], or `null` if no samples exist.
     */
    suspend fun <T : Record> oldestSampleDate(type: RecordType<T>): Instant?

    companion object {

        /**
         * Creates a [HealthClient] instance.
         *
         * If Health Connect is not available on the device, a [NoOpHealthClient] is returned.
         *
         * @param applicationModule The [ApplicationModule] providing application context and standard.
         * @param concurrency The [Concurrency] instance for coroutine dispatching.
         * @param localStorage The [LocalStorage] instance for local data storage.
         * @param appLifecycle The [AppLifecycle] instance for observing application lifecycle events.
         * @param configurations The set of [HealthConfigurationComponent]s to configure the client with.
         *
         * @return A [HealthClient] instance.
         */
        fun create(
            applicationModule: ApplicationModule,
            concurrency: Concurrency,
            localStorage: LocalStorage,
            appLifecycle: AppLifecycle,
            configurations: Set<HealthConfigurationComponent>,
        ): HealthClient {
            val context = applicationModule.requireContext()
            val healthClient = runCatching {
                HealthConnectClient.getOrCreate(context)
            }.getOrNull()

            return if (healthClient != null) {
                DefaultHealthClient(
                    healthConnectClient = healthClient,
                    standard = applicationModule.standard as? HealthConstraint,
                    concurrency = concurrency,
                    localStorage = localStorage,
                    appLifecycle = appLifecycle,
                    configurations = configurations,
                )
            } else {
                NoOpHealthClient()
            }
        }
    }
}
