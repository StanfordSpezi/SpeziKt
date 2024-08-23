package edu.stanford.bdh.engagehf.health.bloodpressure.bottomsheet

import edu.stanford.spezi.core.utils.DateTimeMapperFormatter
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject

class AddBloodPressureBottomSheetUiStateMapper @Inject constructor(
    private val dateTimeMapperFormatter: DateTimeMapperFormatter,
) {

    fun initialUiState(): AddBloodPressureBottomSheetUiState {
        return AddBloodPressureBottomSheetUiState(
            timePickerState = TimePickerState(
                selectedDate = LocalDate.now(),
                selectedTime = LocalTime.now(),
                initialHour = LocalTime.now().hour,
                initialMinute = LocalTime.now().minute,
                selectedDateFormatted = dateTimeMapperFormatter.format(LocalDate.now()),
                selectedTimeFormatted = dateTimeMapperFormatter.format(LocalTime.now())
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
                selectedDateFormatted = dateTimeMapperFormatter.format(date)
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
                selectedTimeFormatted = dateTimeMapperFormatter.format(date)
            )
        )
    }
}
