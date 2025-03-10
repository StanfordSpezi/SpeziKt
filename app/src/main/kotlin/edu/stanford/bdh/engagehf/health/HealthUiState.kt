package edu.stanford.bdh.engagehf.health

import androidx.health.connect.client.records.Record
import edu.stanford.spezi.modules.design.action.PendingActions
import edu.stanford.spezi.ui.StringResource
import java.time.ZonedDateTime

data class HealthUiData(
    val records: List<Record> = emptyList(),
    val chartData: List<AggregatedHealthData> = emptyList(),
    val tableData: List<TableEntryData> = emptyList(),
    val newestData: NewestHealthData? = null,
    val averageData: AverageHealthData? = null,
    val infoRowData: InfoRowData,
    val pendingActions: PendingActions<HealthAction.Async> = PendingActions(),
    val deleteRecordAlertData: DeleteRecordAlertData? = null,
    val valueFormatter: (Double) -> String,

)

sealed interface HealthUiState {
    data object Loading : HealthUiState
    data class NoData(val message: String) : HealthUiState
    data class Success(val data: HealthUiData) : HealthUiState
    data class Error(val message: String) : HealthUiState
}

data class AverageHealthData(
    val value: Double,
    val formattedValue: String,
)

data class AggregatedHealthData(
    val yValues: List<Double>,
    val xValues: List<Double>,
    val seriesName: String,
)

data class NewestHealthData(
    val formattedValue: String,
    val formattedDate: String,
)

data class TableEntryData(
    val id: String?,
    val value: Double?,
    val secondValue: Float?,
    val formattedValues: String,
    val date: ZonedDateTime,
    val formattedDate: String,
    val trend: Double?,
    val formattedTrend: String,
) {
    val isTrendPositive: Boolean?
        get() = trend?.let { it > 0 }
}

data class InfoRowData(
    val formattedValue: String,
    val formattedDate: String,
    val isSelectedTimeRangeDropdownExpanded: Boolean,
    val selectedTimeRange: TimeRange = TimeRange.DAILY,
)

data class DeleteRecordAlertData(
    val recordId: String,
    val title: StringResource,
    val description: StringResource,
    val dismissButton: StringResource,
    val confirmButton: StringResource,
)

sealed interface HealthAction {
    data object DescriptionBottomSheet : HealthAction
    data object DismissConfirmationAlert : HealthAction
    data class RequestDeleteRecord(val recordId: String) : HealthAction
    data class UpdateTimeRange(val timeRange: TimeRange) : HealthAction
    data class ToggleTimeRangeDropdown(val expanded: Boolean) : HealthAction

    sealed interface Async : HealthAction {
        data class DeleteRecord(val recordId: String) : Async
    }
}

enum class RecordType {
    WEIGHT, BLOOD_PRESSURE, HEART_RATE
}
