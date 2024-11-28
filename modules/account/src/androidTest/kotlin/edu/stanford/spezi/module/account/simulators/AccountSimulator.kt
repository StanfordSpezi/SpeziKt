package edu.stanford.spezi.module.account.simulators

import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onNodeWithText
import edu.stanford.spezi.core.testing.waitNode
import edu.stanford.spezi.module.account.composables.provider.TestConfigurationIdentifier

class AccountSimulator(
    val composeTestRule: ComposeTestRule,
) {
    fun waitUntilConfigurationIsDone() {
        composeTestRule.waitNode(TestConfigurationIdentifier.CONTENT)
    }

    fun assertTextExists(text: String) {
        composeTestRule
            .onNodeWithText(text)
            .assertExists()
    }
}
