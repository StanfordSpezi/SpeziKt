package edu.stanford.spezi.core.time

import edu.stanford.spezi.core.Module
import edu.stanford.spezi.core.time.DateFormatter.format
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAccessor
import java.util.Date

/**
 * A utility class for formatting [TemporalAccessor]s
 */
object DateFormatter : Module {

    /**
     * Formats the given [date] using the given [format]
     *
     * @param date the [TemporalAccessor] to format
     * @param format the [DateFormat] to use for formatting
     */
    fun <T : TemporalAccessor> format(date: T, format: DateFormat): String {
        if (date is Instant) return formatDefaultZoneId(date, format)
        return DateTimeFormatter.ofPattern(format.pattern).format(date)
    }

    /**
     * Formats the given [instant] using the given [format]
     *
     * @param instant the [Instant] to format
     * @param format the [DateFormat] to use for formatting
     */
    fun formatUTC(instant: Instant, format: DateFormat): String {
        return format(instant, format, ZoneId.of("UTC"))
    }

    /**
     * Formats the given [instant] using the given [format] and the system default [ZoneId]
     */
    fun formatDefaultZoneId(instant: Instant, format: DateFormat): String {
        return format(instant, format, ZoneId.systemDefault())
    }

    /**
     * Formats the given [date] using the given [format]
     *
     */
    fun format(date: Date, format: DateFormat) = format(date.toInstant(), format)

    /**
     * Formats the given [instant] using the given [format] and [zoneId]
     *
     */
    fun format(instant: Instant, format: DateFormat, zoneId: ZoneId): String {
        return DateTimeFormatter.ofPattern(format.pattern).format(instant.atZone(zoneId))
    }
}

/**
 * Common patterns to use when formatting [TemporalAccessor]s
 */
sealed class DateFormat(open val pattern: String) {
    data object MM_DD_YYYY : DateFormat(pattern = "MM/dd/yyyy")
    data object HH_MM : DateFormat(pattern = "hh:mm")
    data class Custom(override val pattern: String) : DateFormat(pattern)
}
