package edu.stanford.spezi.health

import androidx.health.connect.client.records.Record
import edu.stanford.spezi.core.Standard

/**
 * A [Standard] extension for handling Health Connect data changes.
 */
interface HealthConstraint : Standard {
    /**
     * Called when new Health Connect records of a given type are detected.
     */
    suspend fun <T : Record> handleNewRecords(addedRecords: Set<T>, type: RecordType<out T>)

    /**
     * Called when records are detected as deleted or invalidated.
     */
    suspend fun <T : Record> handleDeletedRecords(deletedRecordIds: Set<String>, type: RecordType<out T>)

    /**
     * Called when Health Connect reports that the stored changes token for this
     * record type has expired. In this state, incremental updates are no longer
     * reliable, so the app must clear its local cache (if any) for this type and perform a
     * full re-sync by fetching all records again.
     */
    suspend fun <T : Record> onFullyResyncRequired(type: RecordType<out T>)
}
