package edu.stanford.bdh.engagehf.health

import androidx.health.connect.client.records.Record
import java.time.ZonedDateTime

data class HealthUiData(
    val records: List<Record> = emptyList(),
    val chartData: List<AggregatedHealthData> = emptyList(),
    val tableData: List<TableEntryData> = emptyList(),
    val newestData: NewestHealthData? = null,
    val averageData: AverageHealthData? = null,
    val infoRowData: InfoRowData,
    val valueFormatter: (Float, TimeRange) -> String,
) {
    val selectedTimeRange = infoRowData.selectedTimeRange
}

sealed interface HealthUiState {
    data object Loading : HealthUiState
    data class NoData(val message: String) : HealthUiState
    data class Success(val data: HealthUiData) : HealthUiState
    data class Error(val message: String) : HealthUiState
}

data class AverageHealthData(
    val value: Float,
    val formattedValue: String,
)

data class AggregatedHealthData(
    val yValues: List<Float>,
    val xValues: List<Float>,
    val seriesName: String,
)

data class NewestHealthData(
    val formattedValue: String,
    val formattedDate: String,
)

data class TableEntryData(
    val id: String?,
    val value: Float,
    val secondValue: Float?,
    val formattedValues: String,
    val date: ZonedDateTime,
    val formattedDate: String,
    val trend: Float,
    val formattedTrend: String,
) {
    val isTrendPositive: Boolean
        get() = trend > 0
}

data class InfoRowData(
    val formattedValue: String,
    val formattedDate: String,
    val isSelectedTimeRangeDropdownExpanded: Boolean,
    val selectedTimeRange: TimeRange = TimeRange.DAILY,
)
