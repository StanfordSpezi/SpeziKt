package edu.stanford.bdh.engagehf.health.bloodpressure.bottomsheet

import edu.stanford.spezi.core.utils.DateFormat
import edu.stanford.spezi.core.utils.DateFormatter
import edu.stanford.spezi.core.utils.TimeProvider
import java.time.Instant
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import javax.inject.Inject

class TimePickerStateMapper @Inject constructor(
    private val dateFormatter: DateFormatter,
    private val timeProvider: TimeProvider,
) {

    fun mapNow(): TimePickerState {
        val now = timeProvider.nowInstant()
        val localTime = timeProvider.nowLocalTime()
        return TimePickerState(
            selectedDate = now,
            selectedTime = localTime,
            initialHour = localTime.hour,
            initialMinute = localTime.minute,
            selectedDateFormatted = format(now),
            selectedTimeFormatted = format(localTime)
        )
    }

    fun mapTime(
        localTime: LocalTime,
        timePickerState: TimePickerState,
    ) = timePickerState.copy(
        selectedTime = localTime,
        selectedTimeFormatted = format(localTime)
    )

    fun mapDate(
        date: Instant,
        timePickerState: TimePickerState,
    ) = timePickerState.copy(
        selectedDate = date,
        selectedDateFormatted = format(date)
    )

    fun mapInstant(timePickerState: TimePickerState): Instant = with(timePickerState) {
        val zoneId = ZoneId.systemDefault()
        val date = selectedDate.atZone(zoneId).toLocalDate()
        return LocalDateTime.of(date, selectedTime).atZone(zoneId).toInstant()
    }

    private fun format(time: LocalTime) = dateFormatter.format(time, DateFormat.HH_MM)
    private fun format(date: Instant): String = dateFormatter.format(date, DateFormat.MM_DD_YYYY)
}
