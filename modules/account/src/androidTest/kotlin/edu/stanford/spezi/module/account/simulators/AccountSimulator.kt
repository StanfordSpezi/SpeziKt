package edu.stanford.spezi.module.account.simulators

import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onNodeWithText
import edu.stanford.spezi.core.testing.onAllNodes
import edu.stanford.spezi.module.account.composables.provider.TestConfigurationIdentifier

class AccountSimulator(
    val composeTestRule: ComposeTestRule,
) {
    fun waitUntilConfigurationCompleted() {
        composeTestRule.waitUntil(1_000) {
            composeTestRule
                .onAllNodes(TestConfigurationIdentifier.CONTENT)
                .fetchSemanticsNodes().size == 1
        }
    }

    fun assertTextExists(text: String) {
        composeTestRule
            .onNodeWithText(text)
            .assertExists()
    }
}
