package edu.stanford.spezi.spezi.core

import com.google.common.truth.Truth.assertThat
import edu.stanford.spezi.spezi.core.logging.SpeziLogger
import org.junit.Test

class SpeziLoggerTest {

    @Test
    fun `GLOBAL_CONFIG must be set to null`() {
        assertThat(SpeziLogger.GLOBAL_CONFIG).isNull()
    }
}
