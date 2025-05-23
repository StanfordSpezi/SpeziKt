package edu.stanford.bdh.engagehf.health.time

import com.google.common.truth.Truth.assertThat
import edu.stanford.spezi.modules.utils.DateFormat
import edu.stanford.spezi.modules.utils.DateFormatter
import edu.stanford.spezi.modules.utils.TimeProvider
import io.mockk.every
import io.mockk.mockk
import org.junit.Before
import org.junit.Test
import java.time.Instant
import java.time.LocalTime

class TimePickerStateMapperTest {
    private val dateFormatter: DateFormatter = mockk()
    private val timeProvider: TimeProvider = mockk()
    private val instant = Instant.now()
    private val time = LocalTime.now()
    private val formattedDate = "date-format"
    private val formattedTime = "time-format"

    private val mapper = TimePickerStateMapper(
        dateFormatter = dateFormatter,
        timeProvider = timeProvider,
    )

    @Before
    fun setup() {
        every { timeProvider.nowInstant() } returns instant
        every { timeProvider.nowLocalTime() } returns time
        every {
            dateFormatter.formatDefaultZoneId(instant, DateFormat.MM_DD_YYYY)
        } returns formattedDate
        every { dateFormatter.format(time, DateFormat.HH_MM) } returns formattedTime
    }

    @Test
    fun `it should map now state correctly`() {
        // when
        val result = mapper.mapNow()

        // then
        with(result) {
            assertThat(selectedDate).isEqualTo(instant)
            assertThat(selectedTime).isEqualTo(time)
            assertThat(initialHour).isEqualTo(time.hour)
            assertThat(initialMinute).isEqualTo(time.minute)
            assertThat(selectedDateFormatted).isEqualTo(formattedDate)
            assertThat(selectedTimeFormatted).isEqualTo(formattedTime)
        }
    }

    @Test
    fun `it should map time correctly`() {
        // given
        val state = mapper.mapNow()
        val newTime = LocalTime.of(1, 1)
        val formatted = "new-formatted-time"
        every { dateFormatter.format(newTime, DateFormat.HH_MM) } returns formatted

        // when
        val result = mapper.mapTime(newTime, state)

        // then
        with(result) {
            assertThat(selectedDate).isEqualTo(instant)
            assertThat(selectedTime).isEqualTo(newTime)
            assertThat(selectedDateFormatted).isEqualTo(formattedDate)
            assertThat(selectedTimeFormatted).isEqualTo(formatted)
        }
    }

    @Test
    fun `it should map date correctly`() {
        // given
        val state = mapper.mapNow()
        val newDate = Instant.now().plusSeconds(12)
        val formatted = "new-formatted-time"
        every { dateFormatter.formatUTC(newDate, DateFormat.MM_DD_YYYY) } returns formatted

        // when
        val result = mapper.mapDate(newDate, state)

        // then
        with(result) {
            assertThat(selectedDate).isEqualTo(newDate)
            assertThat(selectedTime).isEqualTo(time)
            assertThat(selectedDateFormatted).isEqualTo(formatted)
            assertThat(selectedTimeFormatted).isEqualTo(formattedTime)
        }
    }

    @Test
    fun `it should map instant correctly`() {
        // given
        val localTime = LocalTime.of(4, 12)
        val expectedTime = "0${localTime.hour}:${localTime.minute}"
        val state = mapper.mapNow().copy(
            selectedDate = Instant.parse("2025-01-15T23:00:00Z"),
            selectedTime = localTime,
        )
        val expectedInstant = Instant.parse("2025-01-15T$expectedTime:00Z")

        // when
        val result = mapper.mapInstant(state)

        // then
        assertThat(result).isEqualTo(expectedInstant)
    }
}
