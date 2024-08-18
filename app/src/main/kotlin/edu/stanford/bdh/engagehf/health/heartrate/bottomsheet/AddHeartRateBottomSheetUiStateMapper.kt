package edu.stanford.bdh.engagehf.health.heartrate.bottomsheet

import edu.stanford.bdh.engagehf.health.DateTimeMapper
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject

class AddHeartRateBottomSheetUiStateMapper @Inject constructor(
    private val dateTimeMapper: DateTimeMapper,
) {

    fun mapUpdateDateAction(
        date: LocalDate,
        uiState: AddHeartRateBottomSheetUiState,
    ): AddHeartRateBottomSheetUiState {
        return uiState.copy(
            timePickerState = uiState.timePickerState.copy(
                selectedDate = date,
                selectedDateFormatted = dateTimeMapper.formatDate(date)
            )
        )
    }

    fun mapUpdateTimeAction(
        time: LocalTime,
        uiState: AddHeartRateBottomSheetUiState,
    ): AddHeartRateBottomSheetUiState {
        return uiState.copy(
            timePickerState = uiState.timePickerState.copy(
                selectedTime = time,
                selectedTimeFormatted = dateTimeMapper.formatTime(time)
            )
        )
    }
}
