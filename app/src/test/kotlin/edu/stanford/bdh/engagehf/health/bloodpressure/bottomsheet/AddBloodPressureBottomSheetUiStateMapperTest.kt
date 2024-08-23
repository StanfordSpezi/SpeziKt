package edu.stanford.bdh.engagehf.health.bloodpressure.bottomsheet

import com.google.common.truth.Truth.assertThat
import edu.stanford.spezi.core.utils.DateFormatter
import edu.stanford.spezi.core.utils.DateTimeFormatter
import org.junit.Test
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime

class AddBloodPressureBottomSheetUiStateMapperTest {

    private var dateTimeFormatter: DateTimeFormatter =
        DateTimeFormatter(DateFormatter())
    private var uiStateMapper: AddBloodPressureBottomSheetUiStateMapper =
        AddBloodPressureBottomSheetUiStateMapper(dateTimeFormatter)

    @Test
    fun `initialUiState returns correct initial state`() {
        // given
        val expectedDate = LocalDate.now()
        val expectedTime = LocalTime.now()

        // when
        val initialState = uiStateMapper.initialUiState()

        // then
        assertThat(initialState.timePickerState.selectedDate).isEqualTo(expectedDate)
        assertThat(
            Duration.between(
                initialState.timePickerState.selectedTime,
                expectedTime
            ).seconds
        ).isAtMost(2L)
        assertThat(initialState.timePickerState.initialHour).isEqualTo(expectedTime.hour)
        assertThat(initialState.timePickerState.initialMinute).isEqualTo(expectedTime.minute)
        assertThat(initialState.timePickerState.selectedDateFormatted).isEqualTo(
            dateTimeFormatter.format(expectedDate)
        )
        assertThat(initialState.timePickerState.selectedTimeFormatted).isEqualTo(
            dateTimeFormatter.format(expectedTime)
        )
    }

    @Test
    fun `mapUpdateDateAction updates date correctly`() {
        // given
        val initialState = uiStateMapper.initialUiState()
        val newDate = LocalDate.of(2024, 1, 1)

        // when
        val updatedState = uiStateMapper.mapUpdateDateAction(newDate, initialState)

        // then
        assertThat(updatedState.timePickerState.selectedDate).isEqualTo(newDate)
        assertThat(updatedState.timePickerState.selectedDateFormatted).isEqualTo(
            dateTimeFormatter.format(newDate)
        )
    }

    @Test
    fun `mapUpdateTimeAction updates time correctly`() {
        // given
        val initialState = uiStateMapper.initialUiState()
        val newTime = LocalTime.of(14, 30)

        // when
        val updatedState = uiStateMapper.mapUpdateTimeAction(newTime, initialState)

        // then
        assertThat(updatedState.timePickerState.selectedTime).isEqualTo(newTime)
        assertThat(updatedState.timePickerState.selectedTimeFormatted).isEqualTo(
            dateTimeFormatter.format(newTime)
        )
    }
}
