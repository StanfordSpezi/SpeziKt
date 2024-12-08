package edu.stanford.spezi.core.utils

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import java.time.Instant

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
}
