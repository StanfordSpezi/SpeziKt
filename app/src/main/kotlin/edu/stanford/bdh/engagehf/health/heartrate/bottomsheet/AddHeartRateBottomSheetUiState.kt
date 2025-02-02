package edu.stanford.bdh.engagehf.health.heartrate.bottomsheet

import edu.stanford.bdh.engagehf.health.time.TimePickerState

data class AddHeartRateBottomSheetUiState(
    val timePickerState: TimePickerState,
    val heartRate: Int = 60,
) {
    @Suppress("MagicNumber")
    val heartRateRange = 0..200
}
