package edu.stanford.spezi.module.account.simulators

import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onNodeWithText

class AccountOverviewSimulator(
    val composeTestRule: ComposeTestRule,
) {
    fun assertTextExists(text: String) {
        composeTestRule
            .onNodeWithText(text)
            .assertExists()
    }
}
