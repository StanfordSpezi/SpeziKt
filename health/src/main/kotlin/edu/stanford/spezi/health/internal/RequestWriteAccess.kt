package edu.stanford.spezi.health.internal

import androidx.health.connect.client.records.Record
import edu.stanford.spezi.health.AnyRecordType
import edu.stanford.spezi.health.HealthConstraint
import edu.stanford.spezi.health.HealthDataAccessRequirements

/**
 * A [HealthConfigurationComponent] that requests write access
 * to specific Health Connect [Record] types.
 *
 * @important This module already declares all necessary
 * `android.permission.health.WRITE_*` permissions in the Android manifest.
 */
internal class RequestWriteAccess(
    recordTypes: Set<AnyRecordType>,
) : HealthConfigurationComponent {

    /**
     * The [edu.stanford.spezi.health.HealthDataAccessRequirements] requesting write access to the specified record types.
     */
    override val dataAccessRequirements: HealthDataAccessRequirements =
        HealthDataAccessRequirements(read = emptySet(), write = recordTypes)

    /**
     * Creates a [RequestWriteAccess] component requesting write access to the specified Health Connect [Record] types.
     *
     * @param recordTypes The set of [Record] types to request write access for.
     */
    constructor(vararg recordTypes: AnyRecordType) : this(recordTypes.toSet())

    override suspend fun configure(client: DefaultHealthClient, standard: HealthConstraint?) {
        // This component only provides data access requirements,
        // so there is nothing to configure.
    }
}
