package edu.stanford.bdh.engagehf.health

import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

data class HealthUiState(
    val selectedTimeRange: TimeRange = TimeRange.DAILY,
    val healthRecords: List<HealthRecord> = emptyList(),
    val filteredRecords: List<HealthRecord> = emptyList(),
    val aggregatedRecord: List<FilteredHealthData> = emptyList(),
) {
    val averageValue: Float
        get() = healthRecords.map { it.value }.average().toFloat()
}

enum class TimeRange {
    DAILY, WEEKLY, MONTHLY
}

data class HealthRecord(
    val id: String = java.util.UUID.randomUUID().toString(),
    val value: Float,
    val zonedDateTime: ZonedDateTime,
    val trend: Float?,
) {
    val isTrendPositive: Boolean
        get() = trend != null && trend > 0

    val formattedDateAndTime: String
        get() = zonedDateTime.format(DateTimeFormatter.ofPattern("MMM dd, HH:mm"))

    val formattedDate: String
        get() = zonedDateTime.format(DateTimeFormatter.ofPattern("MMM"))

    val formattedValue: String
        get() = "$value lbs"

    val formattedTrend: String
        get() = if (trend != null) {
            if (trend > 0) {
                "▲$trend"
            } else {
                "▼$trend"
            }
        } else {
            ""
        }

    companion object {
        const val KG_TO_LBS_CONVERSION_FACTOR = 2.20462
    }
}
