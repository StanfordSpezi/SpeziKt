package edu.stanford.spezi.core.design.validation.simulators

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick

class FocusValidationRulesSimulator(
    private val composeTestRule: ComposeTestRule,
) {
    private val passwordMessage = "Your password must be at least 8 characters long."
    private val emptyMessage = "This field cannot be empty."

    fun assertHasEngines(hasEngines: Boolean) {
        composeTestRule
            .onNodeWithText("Has Engines: ${if (hasEngines) "Yes" else "No"}")
            .assertExists()
    }

    fun assertInputValid(inputValid: Boolean) {
        composeTestRule
            .onNodeWithText("Input Valid: ${if (inputValid) "Yes" else "No"}")
            .assertExists()
    }

    fun assertPasswordMessageExists(exists: Boolean) {
        val node = composeTestRule.onNodeWithText(passwordMessage)
        if (exists) node.assertExists() else node.assertDoesNotExist()
    }

    fun assertEmptyMessageExists(exists: Boolean) {
        val node = composeTestRule.onNodeWithText(emptyMessage)
        if (exists) node.assertExists() else node.assertDoesNotExist()
    }

    fun clickValidateButton() {
        composeTestRule
            .onNodeWithText("Validate")
            .assertHasClickAction()
            .performClick()
    }

    fun assertLastState(valid: Boolean) {
        composeTestRule
            .onNodeWithText("Last state: ${if (valid) "valid" else "invalid"}")
            .assertExists()
    }
}
