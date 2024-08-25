package edu.stanford.spezi.core.utils

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import java.time.LocalTime

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
}
