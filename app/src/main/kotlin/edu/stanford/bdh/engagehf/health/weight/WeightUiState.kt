package edu.stanford.bdh.engagehf.health.weight

import androidx.health.connect.client.records.WeightRecord
import edu.stanford.bdh.engagehf.health.TimeRange
import java.time.ZonedDateTime

data class WeightUiData(
    val selectedTimeRange: TimeRange = TimeRange.DAILY,
    val isSelectedTimeRangeDropdownExpanded: Boolean = false,
    val weights: List<WeightRecord> = emptyList(),
    val chartWeights: List<WeightData> = emptyList(),
    val tableWeights: List<WeightData> = emptyList(),
    val newestWeight: WeightData? = null,
)

sealed interface WeightUiState {
    data object Loading : WeightUiState
    data class Success(val data: WeightUiData) : WeightUiState
    data class Error(val message: String) : WeightUiState
}

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
