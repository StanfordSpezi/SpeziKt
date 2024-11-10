package edu.stanford.spezi.core.design

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import edu.stanford.spezi.core.design.composables.FocusValidationRules
import edu.stanford.spezi.core.design.simulator.FocusValidationRulesSimulator
import edu.stanford.spezi.core.design.views.validation.state.ValidationContext
import kotlinx.coroutines.delay
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
            // assertEmptyMessageExists(true)
        }
    }

    private fun focusValidationRules(block: FocusValidationRulesSimulator.() -> Unit) {
        FocusValidationRulesSimulator(composeTestRule).apply(block)
    }

}
