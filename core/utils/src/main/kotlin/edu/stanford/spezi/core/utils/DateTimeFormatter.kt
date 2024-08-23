package edu.stanford.spezi.core.utils

import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAccessor
import java.util.Date
import javax.inject.Inject

class DateTimeFormatter @Inject constructor(
    private val formatter: DateFormatter,
) {
    fun format(time: LocalTime) = formatter.format(time, DateFormat.HH_MM)
    fun format(date: LocalDate) = formatter.format(date, DateFormat.MM_DD_YYYY)
    fun format(zonedDateTime: ZonedDateTime) =
        formatter.format(zonedDateTime, DateFormat.Custom("YYYY"))

    fun format(instant: Instant) = formatter.format(instant, DateFormat.Custom("YYYY"))

    fun format(date: Date) = formatter.format(date, DateFormat.HH_MM)
}

sealed class DateFormat(open val pattern: String) {
    data object MM_DD_YYYY : DateFormat(pattern = "MM/dd/yyyy")
    data object HH_MM : DateFormat(pattern = "hh:mm")
    data class Custom(override val pattern: String) : DateFormat(pattern)
}

class DateFormatter @Inject constructor() {

    fun <T : TemporalAccessor> format(date: T, format: DateFormat): String {
        return DateTimeFormatter.ofPattern(format.pattern).format(date)
    }

    fun format(date: Date, format: DateFormat) = format(date.toInstant(), format)
}
