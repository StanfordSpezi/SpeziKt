package edu.stanford.spezi.core.design.personalInfo.simulators

import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTextInput

class NameFieldsTestSimulator(
    private val composeTestRule: ComposeTestRule,
) {

    fun assertTextExists(text: String) {
        composeTestRule
            .onNodeWithText(text)
            .assertExists()
    }

    fun enterText(placeholder: String, text: String) {
        composeTestRule
            .onNodeWithText(placeholder)
            .performTextInput(text)
    }
}
