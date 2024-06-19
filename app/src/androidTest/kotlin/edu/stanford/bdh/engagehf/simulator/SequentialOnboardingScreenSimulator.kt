package edu.stanford.bdh.engagehf.simulator

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.performClick
import edu.stanford.spezi.core.testing.onNodeWithIdentifier
import edu.stanford.spezi.core.testing.waitNode
import edu.stanford.spezi.module.onboarding.sequential.SequentialOnboardingScreenTestIdentifier
import edu.stanford.spezi.module.onboarding.sequential.components.PageIndicatorTestIdentifier

class SequentialOnboardingScreenSimulator(
    private val composeTestRule: ComposeTestRule,
) {
    private val root =
        composeTestRule.onNodeWithIdentifier(SequentialOnboardingScreenTestIdentifier.ROOT)
    private val pager =
        composeTestRule.onNodeWithIdentifier(SequentialOnboardingScreenTestIdentifier.PAGER)
    private val pageIndicator =
        composeTestRule.onNodeWithIdentifier(SequentialOnboardingScreenTestIdentifier.PAGE_INDICATOR)
    private val forwardButton = composeTestRule.onNodeWithIdentifier(PageIndicatorTestIdentifier.FORWARD)

    fun assertIsDisplayed() {
        composeTestRule.waitNode(SequentialOnboardingScreenTestIdentifier.ROOT)
        root.assertIsDisplayed()
    }

    fun assertPagerIsDisplayed() {
        pager.assertIsDisplayed()
    }

    fun assertPageIndicatorIsDisplayed() {
        pageIndicator.assertIsDisplayed()
    }

    fun assertPageTitle(text: String) {
        composeTestRule
            .onNodeWithIdentifier(SequentialOnboardingScreenTestIdentifier.PAGE, text)
            .assertIsDisplayed()
    }

    fun clickForward() {
        forwardButton.performClick()
    }
}
