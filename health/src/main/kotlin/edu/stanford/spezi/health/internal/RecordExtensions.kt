package edu.stanford.spezi.health.internal

import android.health.connect.datatypes.InstantRecord
import android.health.connect.datatypes.IntervalRecord
import android.os.Build
import androidx.health.connect.client.records.Record
import java.time.Instant

/**
 * Retrieves the start time of a [Record] if available.
 *
 * For [IntervalRecord], it returns the [IntervalRecord.startTime].
 * For [InstantRecord], it returns the [InstantRecord.time].
 * For other record types or unsupported Android versions, it returns null.
 */
internal fun Record.startTime(): Instant? {
    return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
        null
    } else {
        when (this) {
            is IntervalRecord -> startTime
            is InstantRecord -> time
            else -> null
        }
    }
}
