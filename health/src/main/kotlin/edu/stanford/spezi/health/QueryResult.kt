package edu.stanford.spezi.health

import androidx.health.connect.client.records.Record

/**
 * Represents the result of a health data query.
 *
 * @param T The type of [Record] being queried.
 * @property added A list of newly added records since the last query.
 * @property deletedIds A list of IDs representing records that have been deleted since the last query.
 * @property nextAnchor An optional anchor string for pagination, allowing subsequent queries to continue from where the last one left off.
 */
data class QueryResult<T : Record>(
    val added: List<T>,
    val deletedIds: List<String>,
    val nextAnchor: String?,
)

/**
 * Enum representing the sorting options for health data queries.
 */
enum class QuerySort {
    NONE,
    BY_START_TIME_ASC,
    BY_START_TIME_DESC,
}
