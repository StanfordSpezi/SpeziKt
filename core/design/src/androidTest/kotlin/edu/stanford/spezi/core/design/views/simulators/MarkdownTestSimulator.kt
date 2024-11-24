package edu.stanford.spezi.core.design.views.simulators

import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText

class MarkdownTestSimulator(
    private val composeTestRule: ComposeTestRule,
) {
    fun assertTextExists(text: String, exists: Boolean = true) {
        val node = composeTestRule.onNodeWithText(text)
        if (exists) {
            node.assertExists()
        } else {
            node.assertDoesNotExist()
        }
    }

    fun waitForTextToAppear(text: String, timeoutMillis: Long = 1_000) {
        composeTestRule.waitUntil(timeoutMillis) {
            composeTestRule.onAllNodesWithText(text)
                .fetchSemanticsNodes().isNotEmpty()
        }
    }
}
