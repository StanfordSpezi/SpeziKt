package edu.stanford.spezi.modules.utils

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAccessor
import java.util.Date
import javax.inject.Inject

class DateFormatter @Inject constructor() {

    fun <T : TemporalAccessor> format(date: T, format: DateFormat): String {
        if (date is Instant) return formatDefaultZoneId(date, format)
        return DateTimeFormatter.ofPattern(format.pattern).format(date)
    }

    fun formatUTC(instant: Instant, format: DateFormat): String {
        return format(instant, format, ZoneId.of("UTC"))
    }

    fun formatDefaultZoneId(instant: Instant, format: DateFormat): String {
        return format(instant, format, ZoneId.systemDefault())
    }

    fun format(date: Date, format: DateFormat) = format(date.toInstant(), format)

    fun format(instant: Instant, format: DateFormat, zoneId: ZoneId): String {
        return DateTimeFormatter.ofPattern(format.pattern).format(instant.atZone(zoneId))
    }
}

sealed class DateFormat(open val pattern: String) {
    data object MM_DD_YYYY : DateFormat(pattern = "MM/dd/yyyy")
    data object HH_MM : DateFormat(pattern = "hh:mm")
    data class Custom(override val pattern: String) : DateFormat(pattern)
}
