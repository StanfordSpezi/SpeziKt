package edu.stanford.spezi.core.utils

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZonedDateTime

class DateTimeFormatterTest {
    private var dateTimeFormatter: DateTimeFormatter = DateTimeFormatter(DateFormatter())

    @Test
    fun `format LocalTime returns correct string`() {
        // given
        val time = LocalTime.of(14, 30)
        val expectedFormat = "02:30"

        // when
        val formattedTime = dateTimeFormatter.format(time)

        // then
        assertThat(formattedTime).isEqualTo(expectedFormat)
    }

    @Test
    fun `format LocalDate returns correct string`() {
        // given
        val date = LocalDate.of(2024, 1, 1)
        val expectedFormat = "01/01/2024"

        // when
        val formattedDate = dateTimeFormatter.format(date)

        // then
        assertThat(formattedDate).isEqualTo(expectedFormat)
    }

    @Test
    fun `format ZonedDateTime returns correct string`() {
        // given
        val zonedDateTime = ZonedDateTime.now()
        val expectedFormat = zonedDateTime.year.toString()

        // when
        val formattedZonedDateTime = dateTimeFormatter.format(zonedDateTime)

        // then
        assertThat(formattedZonedDateTime).isEqualTo(expectedFormat)
    }
}
