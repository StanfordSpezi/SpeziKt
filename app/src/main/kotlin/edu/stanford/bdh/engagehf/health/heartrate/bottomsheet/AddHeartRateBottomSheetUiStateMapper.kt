package edu.stanford.bdh.engagehf.health.heartrate.bottomsheet

import edu.stanford.bdh.engagehf.health.bloodpressure.bottomsheet.TimePickerState
import edu.stanford.spezi.core.utils.DateTimeMapperFormatter
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject

class AddHeartRateBottomSheetUiStateMapper @Inject constructor(
    private val dateTimeMapperFormatter: DateTimeMapperFormatter,
) {

    fun initialUiState(): AddHeartRateBottomSheetUiState {
        val localDate = LocalDate.now()
        val localTime = LocalTime.now()
        return AddHeartRateBottomSheetUiState(
            timePickerState = TimePickerState(
                selectedDate = localDate,
                selectedTime = localTime,
                initialHour = localTime.hour,
                initialMinute = localTime.minute,
                selectedDateFormatted = dateTimeMapperFormatter.format(localDate),
                selectedTimeFormatted = dateTimeMapperFormatter.format(localTime)
            )
        )
    }

    fun mapUpdateDateAction(
        date: LocalDate,
        uiState: AddHeartRateBottomSheetUiState,
    ): AddHeartRateBottomSheetUiState {
        return uiState.copy(
            timePickerState = uiState.timePickerState.copy(
                selectedDate = date,
                selectedDateFormatted = dateTimeMapperFormatter.format(date)
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
                selectedTimeFormatted = dateTimeMapperFormatter.format(time)
            )
        )
    }
}
