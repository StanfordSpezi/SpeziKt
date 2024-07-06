package edu.stanford.bdh.engagehf.simulator

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.ComposeTestRule
import edu.stanford.bdh.engagehf.navigation.screens.AppScreenTestIdentifier
import edu.stanford.spezi.core.testing.onNodeWithIdentifier

class AppSimulator(
    private val composeTestRule: ComposeTestRule,
) {
    private val root = composeTestRule.onNodeWithIdentifier(AppScreenTestIdentifier.ROOT)

    fun assertIsDisplayed() {
        root.assertIsDisplayed()
    }

    fun assertNavigationBarItemIsDisplayed(text: String) {
        composeTestRule
            .onNodeWithIdentifier(AppScreenTestIdentifier.NAVIGATION_BAR_ITEM, text)
            .assertIsDisplayed()
    }
}
