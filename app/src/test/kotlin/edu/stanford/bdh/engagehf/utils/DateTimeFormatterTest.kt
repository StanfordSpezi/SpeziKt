package edu.stanford.bdh.engagehf.utils

import com.google.common.truth.Truth.assertThat
import edu.stanford.spezi.core.utils.DateFormat
import edu.stanford.spezi.core.utils.DateFormatter
import io.mockk.every
import io.mockk.mockk
import org.junit.Test
import java.time.LocalDate
import java.time.LocalTime

class DateTimeFormatterTest {
    private val dateFormatter: DateFormatter = mockk()
    private var dateTimeFormatter: DateTimeFormatter = DateTimeFormatter(dateFormatter)

    @Test
    fun `format LocalTime returns correct string`() {
        // given
        val formattedTime = "formattedTime"
        val time: LocalTime = mockk()
        every { dateFormatter.format(time, DateFormat.HH_MM) } returns formattedTime

        // when
        val result = dateTimeFormatter.format(time)

        // then
        assertThat(result).isEqualTo(formattedTime)
    }

    @Test
    fun `format LocalDate returns correct string`() {
        // given
        val formattedDate = "formatted_date"
        val time: LocalDate = mockk()
        every { dateFormatter.format(time, DateFormat.MM_DD_YYYY) } returns formattedDate

        // when
        val result = dateTimeFormatter.format(time)

        // then
        assertThat(result).isEqualTo(formattedDate)
    }
}
