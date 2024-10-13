package edu.stanford.spezi.core.utils

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class TimeProviderTest {
    private val provider = TimeProvider()

    @Test
    fun `it should indicate system currentTimeMillis`() {
        // given
        val current = System.currentTimeMillis()
        val threshold = 1000L

        // when
        val result = provider.currentTimeMillis()

        // then
        assertThat(result).isAtLeast(current)
        assertThat(result).isAtMost(current + threshold)
    }
}
