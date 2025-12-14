package edu.stanford.spezi.health

import java.time.Instant

/**
 * Represents the time range for collecting health data records
 */
sealed interface CollectionTimeRange {
    /**
     * Collect all new record entries
     */
    data object NewRecords : CollectionTimeRange

    /**
     * Collect record entries starting from a specific date
     * @param date The starting date for collecting records
     */
    data class StartingAt(val date: Instant) : CollectionTimeRange
}
