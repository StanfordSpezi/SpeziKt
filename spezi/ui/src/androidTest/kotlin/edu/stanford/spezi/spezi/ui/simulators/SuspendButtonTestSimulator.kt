package edu.stanford.spezi.spezi.ui.simulators

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick

class SuspendButtonTestSimulator(
    private val composeTestRule: ComposeTestRule,
) {

    fun clickHelloWorldButton() {
        composeTestRule
            .onNodeWithText("Hello World")
            .assertHasClickAction()
            .performClick()
    }

    fun waitForHelloWorldButtonAction() {
        composeTestRule.waitUntil {
            composeTestRule.onAllNodesWithText("Action executed")
                .fetchSemanticsNodes().size == 1
        }

        composeTestRule
            .onNodeWithText("Action executed")
            .assertExists()
    }

    fun resetHelloWorldButtonAction() {
        composeTestRule
            .onNodeWithText("Reset")
            .assertHasClickAction()
            .performClick()
    }

    fun clickHelloThrowingWorldButton() {
        composeTestRule
            .onNodeWithText("Hello Throwing World")
            .assertHasClickAction()
            .assertIsEnabled()
            .performClick()
    }

    fun assertViewStateAlertAppeared(message: String) {
        composeTestRule
            .onNodeWithText("Error")
            .assertExists()

        composeTestRule
            .onNodeWithText(message)
            .assertExists()
    }

    fun dismissViewStateAlert() {
        composeTestRule
            .onNodeWithText("OK")
            .assertHasClickAction()
            .performClick()
    }

    fun assertHelloThrowingWorldButtonIsEnabled() {
        composeTestRule
            .onNodeWithText("Hello Throwing World")
            .assertHasClickAction()
            .assertIsEnabled()
    }
}
