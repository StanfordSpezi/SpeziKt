package edu.stanford.bdh.engagehf.health

sealed interface HealthAction {
    data object DescriptionBottomSheet : HealthAction
    data object AddRecord : HealthAction
    data class DeleteRecord(val recordId: String) : HealthAction
    data class UpdateTimeRange(val timeRange: TimeRange) : HealthAction
    data class ToggleTimeRangeDropdown(val expanded: Boolean) : HealthAction
}
