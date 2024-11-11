package edu.stanford.spezi.core.design.views

import androidx.compose.ui.test.junit4.createComposeRule
import edu.stanford.spezi.core.design.views.composables.FocusValidationRules
import edu.stanford.spezi.core.design.views.simulators.FocusValidationRulesSimulator
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ValidationTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Before
    fun init() {
        composeTestRule.setContent {
            FocusValidationRules()
        }
    }

    @Test
    fun testValidationWithFocus() {
        focusValidationRules {
            assertHasEngines(true)
            assertInputValid(false)
            assertPasswordMessageExists(false)
            assertEmptyMessageExists(false)
            clickValidateButton()
            assertLastState(false)
            assertPasswordMessageExists(true)
            assertEmptyMessageExists(true)
        }
    }

    private fun focusValidationRules(block: FocusValidationRulesSimulator.() -> Unit) {
        FocusValidationRulesSimulator(composeTestRule).apply(block)
    }
}
