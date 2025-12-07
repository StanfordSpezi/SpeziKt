package edu.stanford.spezi.health

import androidx.fragment.app.FragmentActivity
import androidx.health.connect.client.records.Record
import edu.stanford.spezi.core.Module
import edu.stanford.spezi.core.requireDependency
import edu.stanford.spezi.health.internal.HealthClient
import edu.stanford.spezi.health.internal.HealthConfigurationComponent
import edu.stanford.spezi.health.internal.PrivacyConfig
import kotlinx.coroutines.flow.Flow
import java.time.Instant
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

/**
 * The Health module provides access to health data via Health Connect.
 *
 * Register in your Spezi Configuration via:
 *
 * ```kotlin
 * override val configuration = Configuration {
 *    health {
 *        requestReadAccess(RecordType.bloodPressure, RecordType.weight)
 *        requestWriteAccess(RecordType.heartRate)
 *        collectRecord(
 *            recordType = RecordType.bloodPressure,
 *            start = CollectionMode.Manual,
 *            continueInBackground = true,
 *        )
 *    }
 * }
 *
 */
class Health internal constructor(
    private val configComponents: Set<HealthConfigurationComponent>,
    internal val privacyConfig: PrivacyConfig,
) : Module {
    /**
     * The internal [HealthClient] instance, either [edu.stanford.spezi.health.internal.DefaultHealthClient] or
     * [edu.stanford.spezi.health.internal.NoOpHealthClient] in case Health Connect is not available.
     */
    private val healthClient by lazy {
        HealthClient.create(
            applicationModule = requireDependency(),
            concurrency = requireDependency(),
            localStorage = requireDependency(),
            appLifecycle = requireDependency(),
            configurations = configComponents,
        )
    }

    /**
     * Indicates whether all requested permissions have been granted.
     */
    val isFullyAuthorizedState = healthClient.isFullyAuthorizedState

    /**
     * The current configuration state of the Health module.
     */
    val dataAccessRequirements: HealthDataAccessRequirements
        get() = healthClient.dataAccessRequirements

    internal constructor(builder: HealthModuleBuilder) : this(
        configComponents = builder.components.toSet(),
        privacyConfig = builder.privacyConfigBuilder.config,
    )

    internal fun onPermissionsGranted(granted: Set<String>) {
        healthClient.onPermissionsGranted(granted = granted)
    }

    /**
     * Requests all permissions that have not yet been granted.
     *
     * @param activity The [FragmentActivity] to use for requesting permissions.
     */
    fun requestPermissionsIfNeeded(activity: FragmentActivity) {
        healthClient.requestPermissionsIfNeeded(activity = activity)
    }

    /**
     * Requests read permission for the given [Record] [type].
     *
     * @param type The [Record] type to request read permission for.
     * @param activity The [FragmentActivity] to use for requesting permissions.
     */
    fun requestReadPermission(type: AnyRecordType, activity: FragmentActivity) {
        healthClient.requestReadPermission(type = type, activity = activity)
    }

    /**
     * Requests write permission for the given [Record] [type].
     *
     * @param type The [Record] type to request write permission for.
     * @param activity The [FragmentActivity] to use for requesting permissions.
     */
    fun requestWritePermission(type: AnyRecordType, activity: FragmentActivity) {
        healthClient.requestWritePermission(type = type, activity = activity)
    }

    /**
     * Resets the record collection for the given [Record] [type].
     *
     * @param type The [Record] type to reset the collection for.
     */
    fun resetRecordCollection(type: AnyRecordType) {
        healthClient.resetRecordCollection(type)
    }

    /**
     * Inserts a [Record] into Health Connect.
     *
     * @param record The [Record] to insert.
     *
     * @return `true` if the insertion was successful, `false` otherwise.
     */
    suspend fun insert(record: Record): Boolean = healthClient.insert(record)

    /**
     * Queries health data of the given [Record] [type].
     *
     * @param type The [Record] type to query.
     * @param timeRange The [HealthQueryTimeRange] to query data for. Defaults to today's data.
     * @param source The data source to filter by. Defaults to `null`, which includes all sources.
     * @param sortedBy The [QuerySort] to sort the results by. Defaults to [QuerySort.NONE].
     * @param limit The maximum number of records to return. Defaults to [Int.MAX_VALUE].
     * @param predicate An optional predicate to filter the results.
     *
     * @return A list of queried records of the specified [type].
     */
    suspend fun <T : Record> query(
        type: RecordType<T>,
        timeRange: HealthQueryTimeRange = HealthQueryTimeRange.today(),
        source: String? = null,
        sortedBy: QuerySort = QuerySort.NONE,
        limit: Int = Int.MAX_VALUE,
        predicate: ((T) -> Boolean)? = null,
    ): List<T> = healthClient.query(
        type = type,
        timeRange = timeRange,
        source = source,
        sortedBy = sortedBy,
        limit = limit,
        predicate = predicate,
    )

    /**
     * Queries health data of the given [Record] [type] with pagination support.
     *
     * @param type The [Record] type to query.
     * @param timeRange The [HealthQueryTimeRange] to query data for. Defaults to today's data.
     * @param anchor The anchor string to continue querying from a previous result. Defaults to `null`.
     * @param predicate An optional predicate to filter the results.
     *
     * @return A [QueryResult] containing the queried records, deleted record IDs, and the next anchor.
     */
    suspend fun <T : Record> query(
        type: RecordType<T>,
        timeRange: HealthQueryTimeRange = HealthQueryTimeRange.today(),
        anchor: String?,
        predicate: ((T) -> Boolean)? = null,
    ): QueryResult<T> = healthClient.query(
        type = type,
        timeRange = timeRange,
        anchor = anchor,
        predicate = predicate,
    )

    /**
     * Retrieves the oldest sample date for the given [Record] [type].
     *
     * @param type The [Record] type to retrieve the oldest sample date for.
     *
     * @return The oldest sample date as an [Instant], or `null` if no samples exist.
     */
    suspend fun <T : Record> oldestSampleDate(type: RecordType<T>): Instant? = healthClient.oldestSampleDate(type)

    /**
     * Performs a continuous query for health data of the given [Record] [type].
     *
     * @param type The [Record] type to query.
     * @param timeRange The [HealthQueryTimeRange] to query data for.
     * @param interval The interval at which to perform the query. Defaults to 15 seconds.
     * @param anchor The anchor string to continue querying from a previous result. Defaults to `null`.
     * @param predicate An optional predicate to filter the results.
     *
     * @return A [Flow] emitting [QueryResult]s containing the queried records, deleted record IDs, and the next anchor.
     */
    fun <T : Record> continuousQuery(
        type: RecordType<T>,
        timeRange: HealthQueryTimeRange,
        interval: Duration = 15.seconds,
        anchor: String? = null,
        predicate: ((T) -> Boolean)? = null,
    ): Flow<QueryResult<T>> = healthClient.continuousQuery(
        type = type,
        timeRange = timeRange,
        interval = interval,
        anchor = anchor,
        predicate = predicate,
    )

    override fun configure() {
        healthClient.configure()
    }

    /**
     * Checks if the app is authorized to read data of the given [Record] [type].
     *
     * @param type The [Record] type to check read authorization for.
     *
     * @return `true` if the app is authorized to read data of the specified [type], `false` otherwise.
     */
    suspend fun isAuthorizedToRead(type: AnyRecordType): Boolean {
        return healthClient.isAuthorizedToRead(type)
    }

    /**
     * Checks if the app is authorized to write data of the given [Record] [type].
     *
     * @param type The [Record] type to check write authorization for.
     *
     * @return `true` if the app is authorized to write data of the specified [type], `false` otherwise.
     */
    suspend fun isAuthorizedToWrite(type: AnyRecordType): Boolean {
        return healthClient.isAuthorizedToWrite(type)
    }

    /**
     * Represents the configuration state of Health module
     */
    enum class ConfigState {
        Pending,
        Ongoing,
        Completed,
    }
}
