package edu.stanford.bdh.engagehf.health.heartrate.bottomsheet

import edu.stanford.bdh.engagehf.health.bloodpressure.bottomsheet.TimePickerState
import java.time.LocalDate
import java.time.LocalTime

data class AddHeartRateBottomSheetUiState(
    val timePickerState: TimePickerState = TimePickerState(),
    val heartRate: Int = 60,
    val minHeartRate: Int = 0,
    val maxHeartRate: Int = 200,
    val date: LocalDate = LocalDate.now(),
    val time: LocalTime = LocalTime.now(),
)
