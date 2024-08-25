package edu.stanford.bdh.engagehf.health.bloodpressure.bottomsheet

import edu.stanford.bdh.engagehf.utils.DateTimeFormatter
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject

class AddBloodPressureBottomSheetUiStateMapper @Inject constructor(
    private val dateTimeFormatter: DateTimeFormatter,
) {

    fun initialUiState(): AddBloodPressureBottomSheetUiState {
        val localDate = LocalDate.now()
        val localTime = LocalTime.now()
        return AddBloodPressureBottomSheetUiState(
            timePickerState = TimePickerState(
                selectedDate = localDate,
                selectedTime = localTime,
                initialHour = localTime.hour,
                initialMinute = localTime.minute,
                selectedDateFormatted = dateTimeFormatter.format(localDate),
                selectedTimeFormatted = dateTimeFormatter.format(localTime)
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
