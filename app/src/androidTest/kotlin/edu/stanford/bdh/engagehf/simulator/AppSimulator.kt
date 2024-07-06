package edu.stanford.bdh.engagehf.simulator

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.ComposeTestRule
import edu.stanford.bdh.engagehf.navigation.screens.AppScreenTestIdentifier
import edu.stanford.spezi.core.testing.onNodeWithIdentifier

class AppSimulator(
    private val composeTestRule: ComposeTestRule,
) {
    private val root = composeTestRule.onNodeWithIdentifier(AppScreenTestIdentifier.ROOT)

    private val topAppBar =
        composeTestRule.onNodeWithIdentifier(AppScreenTestIdentifier.TOP_APP_BAR)

    private val topAppBarTitle =
        composeTestRule.onNodeWithIdentifier(AppScreenTestIdentifier.TOP_APP_BAR_TITLE)

    fun assertIsDisplayed() {
        root.assertIsDisplayed()
    }

    fun assertTopAppBarIsDisplayed() {
        topAppBar.assertIsDisplayed()
    }

    fun assertTopAppBarTitleIsDisplayed(text: String) {
        topAppBarTitle.assertIsDisplayed()
            .assertTextEquals(text)
    }

    fun assertNavigationBarItemIsDisplayed(text: String) {
        composeTestRule
            .onNodeWithIdentifier(AppScreenTestIdentifier.NAVIGATION_BAR_ITEM, text)
            .assertIsDisplayed()
    }
}
