package edu.stanford.spezi.health

/**
 * Describes the health data access requirements of a [Health] module.
 */
data class HealthDataAccessRequirements(
    /**
     * The set of [RecordType] types that need to be read.
     */
    val read: Set<AnyRecordType>,

    /**
     * The set of [RecordType] types that need to be written.
     */
    val write: Set<AnyRecordType>,
) {

    /**
     * Returns `true` if no read or write access is required.
     */
    val isEmpty: Boolean
        get() = read.isEmpty() && write.isEmpty()

    /**
     * Creates an instance of [HealthDataAccessRequirements] that requires no read or write access.
     */
    constructor() : this(
        read = emptySet(),
        write = emptySet(),
    )

    /**
     * Merges this [HealthDataAccessRequirements] with [other], combining their read and write sets.
     */
    operator fun plus(other: HealthDataAccessRequirements) = copy(
        read = read + other.read,
        write = write + other.write,
    )

    companion object {
        /**
         * An instance of [HealthDataAccessRequirements] that requires no read or write access.
         */
        val Empty = HealthDataAccessRequirements()
    }
}
