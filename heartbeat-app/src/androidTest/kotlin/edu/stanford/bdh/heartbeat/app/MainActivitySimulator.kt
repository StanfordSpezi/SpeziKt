package edu.stanford.bdh.heartbeat.app

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.ComposeTestRule
import edu.stanford.spezi.core.testing.onNodeWithIdentifier

class MainActivitySimulator(rule: ComposeTestRule) {
    private val root = rule.onNodeWithIdentifier(MainActivity.TestIdentifier.ROOT)

    fun assertIsDisplayed() {
        root.assertIsDisplayed()
    }
}
