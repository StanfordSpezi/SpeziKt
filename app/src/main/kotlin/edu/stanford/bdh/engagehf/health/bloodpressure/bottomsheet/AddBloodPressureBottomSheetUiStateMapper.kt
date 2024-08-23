package edu.stanford.bdh.engagehf.health.bloodpressure.bottomsheet

import edu.stanford.bdh.engagehf.health.DateTimeMapper
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject

class AddBloodPressureBottomSheetUiStateMapper @Inject constructor(
    private val dateTimeMapper: DateTimeMapper,
) {

    fun initialUiState(): AddBloodPressureBottomSheetUiState {
        return AddBloodPressureBottomSheetUiState(
            timePickerState = TimePickerState(
                selectedDate = LocalDate.now(),
                selectedTime = LocalTime.now(),
                initialHour = LocalTime.now().hour,
                initialMinute = LocalTime.now().minute,
                selectedDateFormatted = dateTimeMapper.formatDate(LocalDate.now()),
                selectedTimeFormatted = dateTimeMapper.formatTime(LocalTime.now())
            )
        )
    }

    fun mapUpdateDateAction(
        date: LocalDate,
        uiState: AddBloodPressureBottomSheetUiState,
    ): AddBloodPressureBottomSheetUiState {
        return uiState.copy(
            timePickerState = uiState.timePickerState.copy(
                selectedDate = date,
                selectedDateFormatted = dateTimeMapper.formatDate(date)
            )
        )
    }

    fun mapUpdateTimeAction(
        date: LocalTime,
        uiState: AddBloodPressureBottomSheetUiState,
    ): AddBloodPressureBottomSheetUiState {
        return uiState.copy(
            timePickerState = uiState.timePickerState.copy(
                selectedTime = date,
                selectedTimeFormatted = dateTimeMapper.formatTime(date)
            )
        )
    }
}
