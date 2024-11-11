package edu.stanford.spezi.core.design.personalInfo.simulators

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
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

    fun assertImageExists() {
        composeTestRule
            .onAllNodesWithContentDescription("")
            .assertCountEquals(1)
    }
}
