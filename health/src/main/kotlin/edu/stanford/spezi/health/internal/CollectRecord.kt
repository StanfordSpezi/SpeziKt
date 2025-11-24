package edu.stanford.spezi.health.internal

import androidx.health.connect.client.records.Record
import edu.stanford.spezi.health.AnyRecordType
import edu.stanford.spezi.health.CollectionMode
import edu.stanford.spezi.health.CollectionTimeRange
import edu.stanford.spezi.health.HealthConstraint
import edu.stanford.spezi.health.healthLogger

/**
 * Configuration component to collect Health data of a specific [recordType] by registering a [HealthDataCollector]
 * to the [DefaultHealthClient].
 *
 * Enables real-time or periodic Health data collection and delivery to a [edu.stanford.spezi.health.HealthConstraint].
 */
internal class CollectRecord(
    private val recordType: AnyRecordType,
    private val start: CollectionMode = CollectionMode.Manual,
    private val continueInBackground: Boolean = false,
    private val timeRange: CollectionTimeRange = CollectionTimeRange.NewRecords,
    private val predicate: ((Record) -> Boolean)? = null,
) : HealthConfigurationComponent {
    private val logger by healthLogger()

    override val dataAccessRequirements =
        HealthDataAccessRequirements(read = setOf(recordType), write = emptySet())

    override suspend fun configure(client: DefaultHealthClient, standard: HealthConstraint?) {
        if (standard == null) {
            logger.w { "No standard HealthConstraint provided, skipping CollectRecord for $recordType" }
            return
        }
        val collector = HealthDataCollector(
            recordType = recordType,
            tokenStore = client.changesTokenStore,
            healthConstraint = standard,
            timeRange = timeRange,
            predicate = predicate,
            client = client.healthConnectClient,
            scope = client.ioScope,
            deliverySetting = HealthDataCollectorDeliverySetting(
                collectionMode = start,
                continueInBackground = continueInBackground
            )
        )
        client.addHealthDataCollector(collector)
    }
}
