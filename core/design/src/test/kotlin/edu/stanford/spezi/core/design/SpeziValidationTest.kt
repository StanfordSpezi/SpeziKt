package edu.stanford.spezi.core.design

import com.google.common.truth.Truth.assertThat
import edu.stanford.spezi.core.design.views.validation.ValidationEngine
import edu.stanford.spezi.core.design.views.validation.ValidationRule
import edu.stanford.spezi.core.design.views.validation.nonEmpty
import kotlinx.coroutines.CoroutineScope
import org.junit.Test

class SpeziValidationTest {

    @Test
    fun testValidationDebounce() {
        val engine = ValidationEngine(rules = listOf(ValidationRule.nonEmpty))

        engine.submit("Valid")
        assertThat(engine.inputValid).isTrue()
        assertThat(engine.validationResults).isEmpty()

        engine.submit("", debounce = true)
        assertThat(engine.inputValid).isTrue()
        assertThat(engine.validationResults).isEmpty()

        Thread.sleep(1_000)

        assertThat(engine.inputValid).isFalse()
        assertThat(engine.validationResults).hasSize(1)

        engine.submit("Valid", debounce = true)
        assertThat(engine.inputValid).isTrue() // valid state is reported instantly
        assertThat(engine.validationResults).isEmpty()
    }

}
