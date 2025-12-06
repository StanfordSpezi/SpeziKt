package edu.stanford.spezi.health.internal

import androidx.health.connect.client.records.Record
import edu.stanford.spezi.health.AnyRecordType
import edu.stanford.spezi.health.HealthConstraint

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
     * The [HealthDataAccessRequirements] requesting write access to the specified record types.
     */
    override val dataAccessRequirements: HealthDataAccessRequirements =
        HealthDataAccessRequirements(read = emptySet(), write = recordTypes)

    /**
     * Creates a [RequestWriteAccess] component requesting write access to the specified Health Connect [Record] types.
     *
     * @param recordTypes The set of [Record] types to request write access for.
     */
    constructor(vararg recordTypes: AnyRecordType) : this(recordTypes.toSet())

    /**
     * Creates a [RequestWriteAccess] component requesting write access to the specified Health Connect [Record] types,
     * categorized by their type.
     *
     * @param quantity  The set of quantity [Record] types to request write access for.
     * @param category  The set of category [Record] types to request write access for.
     * @param correlation The set of correlation [Record] types to request write access for.
     * @param other     The set of other [Record] types to request write access for.
     */
    constructor(
        quantity: Set<AnyRecordType> = emptySet(),
        category: Set<AnyRecordType> = emptySet(),
        correlation: Set<AnyRecordType> = emptySet(),
        other: Set<AnyRecordType> = emptySet(),
    ) : this(recordTypes = quantity + category + correlation + other)

    override suspend fun configure(client: DefaultHealthClient, standard: HealthConstraint?) {
        // This component only provides data access requirements,
        // so there is nothing to configure.
    }
}
