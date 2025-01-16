package edu.stanford.spezi.core.utils

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import java.time.Instant
import java.time.LocalTime
import java.util.Date

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
        val instant = Instant.parse("2025-01-15T23:45:00Z")
        val expectedFormat = "01/15/2025"

        // when
        val result = formatter.format(instant, DateFormat.MM_DD_YYYY)

        // then
        assertThat(result).isEqualTo(expectedFormat)
    }

    @Test
    fun `it should format date correctly`() {
        // given
        val date = Date.from(Instant.parse("2025-01-15T18:45:00Z"))
        val expectedFormat = "01/15/2025"

        // when
        val result = formatter.format(date, DateFormat.MM_DD_YYYY)

        // then
        assertThat(result).isEqualTo(expectedFormat)
    }
}
