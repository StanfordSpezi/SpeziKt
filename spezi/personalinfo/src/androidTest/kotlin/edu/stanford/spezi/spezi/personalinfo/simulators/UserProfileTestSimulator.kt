package edu.stanford.spezi.spezi.personalinfo.simulators

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText

class UserProfileTestSimulator(
    private val composeTestRule: ComposeTestRule,
) {

    fun assertUserInitialsExists(exists: Boolean, text: String) {
        val node = composeTestRule
            .onNodeWithText(text)
        if (exists) {
            node.assertExists()
        } else {
            node.assertDoesNotExist()
        }
    }

    fun waitUntilUserInitialsDisappear(text: String, timeoutMillis: Long = 1_000) {
        composeTestRule.waitUntil(timeoutMillis = timeoutMillis) {
            composeTestRule.onAllNodesWithText(text)
                .fetchSemanticsNodes().isEmpty()
        }
    }

    fun assertImageExists(contentDescription: String) {
        composeTestRule
            .onAllNodesWithContentDescription(contentDescription)
            .assertCountEquals(1)
    }
}
