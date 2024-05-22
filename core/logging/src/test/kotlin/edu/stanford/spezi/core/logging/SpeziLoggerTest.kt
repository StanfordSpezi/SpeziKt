package edu.stanford.spezi.core.logging

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class SpeziLoggerTest {

    @Test
    fun `GLOBAL_CONFIG must be set to null`() {
        assertThat(SpeziLogger.GLOBAL_CONFIG).isNull()
    }
}