package edu.stanford.spezi.ui.validation

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Test

class SpeziValidationTest {
    private val dispatcher = StandardTestDispatcher()

    @Test
    fun testValidationDebounce() = runTest(dispatcher) {
        val engine = ValidationEngineImpl(
            rules = listOf(ValidationRule.nonEmpty),
            coroutineScope = TestScope(dispatcher)
        )

        engine.submit("Valid")
        assertThat(engine.inputValid).isTrue()
        assertThat(engine.validationResults).isEmpty()

        engine.submit("", debounce = true)
        assertThat(engine.inputValid).isTrue()
        assertThat(engine.validationResults).isEmpty()

        delay(1_000)

        assertThat(engine.inputValid).isFalse()
        assertThat(engine.validationResults).hasSize(1)

        engine.submit("Valid", debounce = true)
        assertThat(engine.inputValid).isTrue() // valid state is reported instantly
        assertThat(engine.validationResults).isEmpty()
    }
}
