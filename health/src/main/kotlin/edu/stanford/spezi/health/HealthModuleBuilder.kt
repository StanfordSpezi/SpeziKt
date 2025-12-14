package edu.stanford.spezi.health

import androidx.health.connect.client.records.Record
import edu.stanford.spezi.core.SpeziDsl
import edu.stanford.spezi.health.internal.CollectRecord
import edu.stanford.spezi.health.internal.HealthConfigurationComponent
import edu.stanford.spezi.health.internal.RequestReadAccess
import edu.stanford.spezi.health.internal.RequestWriteAccess

/**
 * Builder for configuring the [Health] module.
 */
@SpeziDsl
class HealthModuleBuilder {
    @PublishedApi
    internal val components = mutableSetOf<HealthConfigurationComponent>()

    @PublishedApi
    internal val privacyConfigBuilder = PrivacyConfigBuilder()

    /**
     * Adds a [requestReadAccess] component for the given [Record] types.
     */
    fun requestReadAccess(recordTypes: Set<AnyRecordType>) {
        components.add(RequestReadAccess(recordTypes = recordTypes))
    }

    /**
     * Adds a [requestReadAccess] component for the given [Record] types.
     */
    fun requestReadAccess(vararg recordTypes: AnyRecordType) {
        this@HealthModuleBuilder.requestReadAccess(recordTypes.toSet())
    }

    /**
     * Adds a [requestWriteAccess] component for the given [Record] types.
     */
    fun requestWriteAccess(recordTypes: Set<AnyRecordType>) {
        components.add(RequestWriteAccess(recordTypes = recordTypes))
    }

    /**
     * Adds a [requestWriteAccess] component for the given [Record] types.
     */
    fun requestWriteAccess(vararg recordTypes: AnyRecordType) {
        this@HealthModuleBuilder.requestWriteAccess(recordTypes.toSet())
    }

    /**
     * Adds a [edu.stanford.spezi.health.internal.CollectRecord] component to collect records of the specified [recordType].
     *
     * @param recordType The type of [Record] to collect.
     * @param start The [CollectionMode] to determine when to start collecting records. Defaults to [CollectionMode.Manual].
     * @param continueInBackground Whether to continue collecting records in the background. Defaults to `false`.
     * @param timeRange The [CollectionTimeRange] specifying which records to collect. Defaults to [CollectionTimeRange.NewRecords].
     * @param predicate An optional predicate to filter collected records.
     */
    fun collectRecord(
        recordType: AnyRecordType,
        start: CollectionMode = CollectionMode.Manual,
        continueInBackground: Boolean = false,
        timeRange: CollectionTimeRange = CollectionTimeRange.NewRecords,
        predicate: ((Record) -> Boolean)? = null,
    ) {
        components.add(
            CollectRecord(
                recordType = recordType,
                start = start,
                continueInBackground = continueInBackground,
                timeRange = timeRange,
                predicate = predicate,
            )
        )
    }

    /**
     * Configures the privacy configuration for the Health module. If none is provided, a default privacy configuration will be used.
     *
     * Example usages:
     *
     * ```kotlin
     * privacy {
     *     // Simple explanation text
     *     explanationText(
     *         title = StringResource("Health Data Access"),
     *         description = StringResource("This app uses Health Connect to read and write health data.")
     *     )
     *     // or a custom composable
     *     composable { PrivacyComposableScreen() }
     *
     *     // or a composable content
     *     content(PrivacyComposableContent())
     * }
     * ```
     */
    fun privacy(block: PrivacyConfigBuilder.() -> Unit) {
        privacyConfigBuilder.apply(block)
    }
}
