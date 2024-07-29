package edu.stanford.bdh.engagehf.health.weight

import androidx.health.connect.client.records.Record
import edu.stanford.bdh.engagehf.health.TimeRange
import java.time.ZonedDateTime

data class WeightUiData(
    val selectedTimeRange: TimeRange = TimeRange.DAILY,
    val isSelectedTimeRangeDropdownExpanded: Boolean = false,
    val weights: List<Record> = emptyList(),
    val chartWeights: List<AggregatedWeightData> = emptyList(),
    val tableWeights: List<WeightData> = emptyList(),
    val newestWeight: NewestWeightData? = null,
    val averageWeight: AverageWeightData? = null,
) {
    val xValues = chartWeights.map { it.xValue }

    val yValues = chartWeights.map { it.yValue }
}

sealed interface WeightUiState {
    data object Loading : WeightUiState
    data class Success(val data: WeightUiData) : WeightUiState
    data class Error(val message: String) : WeightUiState
}

data class AverageWeightData(
    val value: Float,
    val formattedValue: String,
)

data class AggregatedWeightData(
    val yValue: Float,
    val xValue: Float,
)

data class NewestWeightData(
    val formattedValue: String,
    val formattedDate: String,
)

data class WeightData(
    val id: String?,
    val value: Float,
    val formattedValue: String,
    val date: ZonedDateTime,
    val formattedDate: String,
    val xAxis: Float,
    val trend: Float,
    val formattedTrend: String,
) {
    val isTrendPositive: Boolean
        get() = trend > 0
}
