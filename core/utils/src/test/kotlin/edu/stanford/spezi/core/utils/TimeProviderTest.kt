package edu.stanford.spezi.core.utils

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import java.time.Instant
import java.time.LocalTime

class TimeProviderTest {
    private val provider = TimeProvider()

    @Test
    fun `it should indicate now instant epoch millis`() {
        // given
        val current = Instant.now().toEpochMilli()
        val threshold = 1000L

        // when
        val result = provider.currentTimeMillis()

        // then
        assertThat(result).isAtLeast(current)
        assertThat(result).isAtMost(current + threshold)
    }

    @Test
    fun `it should indicate now instant`() {
        // given
        val current = Instant.now()
        val threshold = 1000L

        // when
        val result = provider.nowInstant()

        // then
        assertThat(result.toEpochMilli()).isAtLeast(current.toEpochMilli())
        assertThat(result.toEpochMilli()).isAtMost(current.toEpochMilli() + threshold)
    }

    @Test
    fun `it should indicate now local time`() {
        // given
        val current = LocalTime.now()
        val threshold = 1

        // when
        val result = provider.nowLocalTime()

        // then
        assertThat(result.hour).isAtLeast(current.hour)
        assertThat(result.hour).isAtMost(current.hour + threshold)
        assertThat(result.minute).isAtLeast(current.minute)
        assertThat(result.minute).isAtMost(current.minute + threshold)
    }
}
