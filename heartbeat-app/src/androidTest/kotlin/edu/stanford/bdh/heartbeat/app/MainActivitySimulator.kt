package edu.stanford.bdh.heartbeat.app

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.ComposeTestRule
import edu.stanford.spezi.core.testing.onNodeWithIdentifier

class MainActivitySimulator(rule: ComposeTestRule) {
    private val root = rule.onNodeWithIdentifier(MainActivity.TestIdentifier.ROOT)
    private val title = rule.onNodeWithIdentifier(MainActivity.TestIdentifier.TEXT)

    fun assertIsDisplayed() {
        root.assertIsDisplayed()
    }

    fun assertText(text: String) {
        title
            .assertIsDisplayed()
            .assertTextEquals(text)
    }
}
