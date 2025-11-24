package edu.stanford.spezi.health

import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

/**
 * Represents a time range for querying health data.
 *
 * @property start The start time of the range.
 * @property end The end time of the range.
 */
data class HealthQueryTimeRange(
    val start: Instant,
    val end: Instant,
) {

    /**
     * The duration of the time range.
     */
    val duration: Duration
        get() = (end.toEpochMilli() - start.toEpochMilli()).milliseconds

    /**
     * Indicates whether the time range is open-ended (i.e., has no end).
     */
    val isOpenEnded: Boolean
        get() = end == Instant.MAX

    @Suppress("TooManyFunctions")
    companion object {

        /**
         * The range representing all time.
         */
        fun ever() = HealthQueryTimeRange(Instant.MIN, Instant.MAX)

        /**
         * The range starting at [start] and extending to the maximum representable time.
         */
        fun startingAt(start: Instant) = HealthQueryTimeRange(start, Instant.MAX)

        /**
         * The range ending at [end] and starting from the minimum representable time.
         */
        fun last(duration: Duration): HealthQueryTimeRange {
            val end = Instant.now()
            val start = end.minusMillis(duration.inWholeMilliseconds)
            return HealthQueryTimeRange(start, end)
        }

        /**
         * The range starting from now and extending for the given [duration].
         */
        fun until(duration: Duration): HealthQueryTimeRange {
            val start = Instant.now()
            val end = start.plusMillis(duration.inWholeMilliseconds)
            return HealthQueryTimeRange(start, end)
        }

        /**
         * The range representing the current hour.
         */
        fun currentHour(): HealthQueryTimeRange {
            val now = ZonedDateTime.now()
            val start = now.truncatedTo(ChronoUnit.HOURS)
            val end = start.plusHours(1)
            return HealthQueryTimeRange(start.toInstant(), end.toInstant())
        }

        /**
         * The range representing today.
         */
        fun today(): HealthQueryTimeRange {
            val zone = ZoneId.systemDefault()
            val now = LocalDate.now(zone)
            val start = now.atStartOfDay(zone)
            val end = start.plusDays(1)
            return HealthQueryTimeRange(start.toInstant(), end.toInstant())
        }

        /**
         * The range representing the current week.
         */
        fun currentWeek(): HealthQueryTimeRange {
            val zone = ZoneId.systemDefault()
            val now = LocalDate.now(zone)
            val start = now.with(DayOfWeek.MONDAY).atStartOfDay(zone)
            val end = start.plusWeeks(1)
            return HealthQueryTimeRange(start.toInstant(), end.toInstant())
        }

        /**
         * The range representing the current month.
         */
        fun currentMonth(): HealthQueryTimeRange {
            val zone = ZoneId.systemDefault()
            val now = LocalDate.now(zone)
            val start = now.withDayOfMonth(1).atStartOfDay(zone)
            val end = start.plusMonths(1)
            return HealthQueryTimeRange(start.toInstant(), end.toInstant())
        }

        /**
         * The range representing the current year.
         */
        fun currentYear(): HealthQueryTimeRange {
            val zone = ZoneId.systemDefault()
            val now = LocalDate.now(zone)
            val start = now.withDayOfYear(1).atStartOfDay(zone)
            val end = start.plusYears(1)
            return HealthQueryTimeRange(start.toInstant(), end.toInstant())
        }

        /**
         * The range representing the last [hours] hours.
         */
        fun lastHours(hours: Long): HealthQueryTimeRange {
            require(hours >= 1) { "hours must be >= 1" }
            val end = Instant.now()
            val start = end.minus(hours, ChronoUnit.HOURS)
            return HealthQueryTimeRange(start, end)
        }

        /**
         * The range representing the last [days] days.
         */
        fun lastDays(days: Long): HealthQueryTimeRange {
            require(days >= 1) { "days must be >= 1" }
            val end = Instant.now()
            val start = end.minus(days, ChronoUnit.DAYS)
            return HealthQueryTimeRange(start, end)
        }

        /**
         * The range representing the last [weeks] weeks.
         */
        fun lastWeeks(weeks: Long): HealthQueryTimeRange {
            require(weeks >= 1) { "weeks must be >= 1" }
            val end = Instant.now()
            val start = end.minus(weeks, ChronoUnit.WEEKS)
            return HealthQueryTimeRange(start, end)
        }

        /**
         * The range representing the last [months] months.
         */
        fun lastMonths(months: Long): HealthQueryTimeRange {
            require(months >= 1) { "months must be >= 1" }
            val zone = ZoneId.systemDefault()
            val now = LocalDate.now(zone)
            val start = now.minusMonths(months - 1).withDayOfMonth(1).atStartOfDay(zone)
            val end = start.plusMonths(months)
            return HealthQueryTimeRange(start.toInstant(), end.toInstant())
        }

        /**
         * The range representing the last [years] years.
         */
        fun lastYears(years: Long): HealthQueryTimeRange {
            require(years >= 1) { "years must be >= 1" }
            val zone = ZoneId.systemDefault()
            val now = LocalDate.now(zone)
            val start = now.minusYears(years - 1).withDayOfYear(1).atStartOfDay(zone)
            val end = start.plusYears(years)
            return HealthQueryTimeRange(start.toInstant(), end.toInstant())
        }
    }
}
