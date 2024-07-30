package edu.stanford.bdh.engagehf.health.weight

import edu.stanford.bdh.engagehf.health.TimeRange

sealed interface Action {
    data object DescriptionBottomSheet : Action
    data object AddRecord : Action
    data class DeleteRecord(val recordId: String) : Action
    data class UpdateTimeRange(val timeRange: TimeRange) : Action
    data class ToggleTimeRangeDropdown(val expanded: Boolean) : Action
}
