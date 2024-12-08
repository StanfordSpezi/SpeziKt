package edu.stanford.spezi.module.onboarding.spezi.simulators

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick

class WelcomeOnboardingSimulator(
    private val composeTestRule: ComposeTestRule,
) {
    fun assertTextExists(text: String) {
        composeTestRule
            .onNodeWithText(text)
            .assertExists()
    }

    fun clickButton(text: String) {
        composeTestRule
            .onNodeWithText(text)
            .assertHasClickAction()
            .performClick()
    }
}
