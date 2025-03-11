package edu.stanford.spezi.modules.utils

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import java.time.Instant
import java.time.LocalTime
import java.time.ZoneId

class DateFormatterTest {
    private val formatter = DateFormatter()

    @Test
    fun `it should format time correctly`() {
        // given
        val localTime = LocalTime.of(1, 1)

        // when
        val result = formatter.format(localTime, DateFormat.HH_MM)

        // then
        assertThat(result).isEqualTo("01:01")
    }

    @Test
    fun `it should format instant correctly`() {
        // given
        val instant = Instant.parse("2025-01-15T18:45:00Z")
        val expectedFormat = "01/15/2025"

        // when
        val result = formatter.format(instant, DateFormat.MM_DD_YYYY)

        // then
        assertThat(result).isEqualTo(expectedFormat)
    }

    @Test
    fun `it should format instant in system zone id correctly`() {
        // given
        val instant = Instant.parse("2025-01-15T18:45:00Z")
        val expectedFormat = "01/15/2025"

        // when
        val result = formatter.formatDefaultZoneId(instant, DateFormat.MM_DD_YYYY)

        // then
        assertThat(result).isEqualTo(expectedFormat)
    }

    @Test
    fun `it should format instant in UTC correctly`() {
        // given
        val instant = Instant.parse("2025-01-15T22:00:00-08:00")
        val expectedFormat = "01/16/2025"

        // when
        val result = formatter.formatUTC(instant, DateFormat.MM_DD_YYYY)

        // then
        assertThat(result).isEqualTo(expectedFormat)
    }

    @Test
    fun `it should format instant in Los Angeles time zone correctly correctly`() {
        // given
        val instant = Instant.parse("2025-01-15T01:00:00Z")
        val expectedFormat = "01/14/2025"
        val zoneId = ZoneId.of("America/Los_Angeles")

        // when
        val result = formatter.format(instant, DateFormat.MM_DD_YYYY, zoneId)

        // then
        assertThat(result).isEqualTo(expectedFormat)
    }

    @Test
    fun `it should format zoned date correctly`() {
        // given
        val instantDate = 15
        val expectedZonedDate = instantDate - 1
        val zoned = Instant
            .parse("2025-01-${instantDate}T03:45:00Z")
            .atZone(ZoneId.of("America/Los_Angeles"))
        val expectedFormat = "01/$expectedZonedDate/2025"

        // when
        val result = formatter.format(zoned, DateFormat.MM_DD_YYYY)

        // then
        assertThat(result).isEqualTo(expectedFormat)
    }
}
