package edu.stanford.bdh.engagehf.health.weight.bottomsheet

import edu.stanford.bdh.engagehf.health.bloodpressure.bottomsheet.TimePickerState
import edu.stanford.spezi.core.utils.DateTimeMapperFormatter
import edu.stanford.spezi.core.utils.LocaleProvider
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject

class AddWeightBottomSheetUiStateMapper @Inject constructor(
    private val dateTimeMapperFormatter: DateTimeMapperFormatter,
    private val localeProvider: LocaleProvider,
) {

    fun mapInitialUiState(): AddWeightBottomSheetUiState {
        val localDate = LocalDate.now()
        val localTime = LocalTime.now()
        return AddWeightBottomSheetUiState(
            timePickerState = TimePickerState(
                selectedDate = localDate,
                selectedTime = localTime,
                initialHour = localTime.hour,
                initialMinute = localTime.minute,
                selectedDateFormatted = dateTimeMapperFormatter.format(localDate),
                selectedTimeFormatted = dateTimeMapperFormatter.format(localTime)
            ),
            weightUnit = when (localeProvider.getDefaultLocale().country) {
                "US", "LR", "MM" -> WeightUnit.LBS
                else -> WeightUnit.KG
            }
        )
    }

    fun mapUpdateDateAction(
        date: LocalDate,
        uiState: AddWeightBottomSheetUiState,
    ): AddWeightBottomSheetUiState {
        return uiState.copy(
            timePickerState = uiState.timePickerState.copy(
                selectedDate = date,
                selectedDateFormatted = dateTimeMapperFormatter.format(date)
            )
        )
    }

    fun mapUpdateTimeAction(
        time: LocalTime,
        uiState: AddWeightBottomSheetUiState,
    ): AddWeightBottomSheetUiState {
        return uiState.copy(
            timePickerState = uiState.timePickerState.copy(
                selectedTime = time,
                selectedTimeFormatted = dateTimeMapperFormatter.format(time)
            )
        )
    }
}
