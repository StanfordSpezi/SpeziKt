package edu.stanford.bdh.engagehf.health.bloodpressure.bottomsheet

import edu.stanford.spezi.core.utils.DateTimeFormatter
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject

class AddBloodPressureBottomSheetUiStateMapper @Inject constructor(
    private val dateTimeFormatter: DateTimeFormatter,
) {

    fun initialUiState(): AddBloodPressureBottomSheetUiState {
        return AddBloodPressureBottomSheetUiState(
            timePickerState = TimePickerState(
                selectedDate = LocalDate.now(),
                selectedTime = LocalTime.now(),
                initialHour = LocalTime.now().hour,
                initialMinute = LocalTime.now().minute,
                selectedDateFormatted = dateTimeFormatter.format(LocalDate.now()),
                selectedTimeFormatted = dateTimeFormatter.format(LocalTime.now())
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
                selectedDateFormatted = dateTimeFormatter.format(date)
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
                selectedTimeFormatted = dateTimeFormatter.format(date)
            )
        )
    }
}
