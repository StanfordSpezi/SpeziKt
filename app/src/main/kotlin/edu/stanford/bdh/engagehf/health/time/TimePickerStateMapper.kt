package edu.stanford.bdh.engagehf.health.time

import edu.stanford.spezi.modules.utils.DateFormat
import edu.stanford.spezi.modules.utils.DateFormatter
import edu.stanford.spezi.modules.utils.TimeProvider
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
            selectedDateFormatted = dateFormatter.formatDefaultZoneId(now, DATE_FORMAT),
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
        selectedDateFormatted = dateFormatter.formatUTC(date, DATE_FORMAT)
    )

    fun mapInstant(timePickerState: TimePickerState): Instant = with(timePickerState) {
        val date = selectedDate.atZone(UTC_ZONE_ID).toLocalDate()
        return LocalDateTime.of(date, selectedTime).atZone(UTC_ZONE_ID).toInstant()
    }

    private fun format(time: LocalTime) = dateFormatter.format(time, DateFormat.HH_MM)

    private companion object {
        val UTC_ZONE_ID: ZoneId = ZoneId.of("UTC")
        val DATE_FORMAT = DateFormat.MM_DD_YYYY
    }
}
