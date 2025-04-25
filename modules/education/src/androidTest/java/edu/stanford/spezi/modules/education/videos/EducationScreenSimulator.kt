package edu.stanford.spezi.modules.education.videos

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.ComposeTestRule
import edu.stanford.spezi.ui.testing.onNodeWithIdentifier

class EducationScreenSimulator(composeTestRule: ComposeTestRule) {

    private val videoSection =
        composeTestRule.onNodeWithIdentifier(EducationScreenTestIdentifier.VIDEO_SECTION)

    private val retryButton =
        composeTestRule.onNodeWithIdentifier(EducationScreenTestIdentifier.RETRY_BUTTON)

    private val loadingRoot =
        composeTestRule.onNodeWithIdentifier(EducationScreenTestIdentifier.LOADING_ROOT)

    fun assertLoading() = loadingRoot.assertIsDisplayed()

    fun assertVideoSection() = videoSection.assertIsDisplayed()

    fun assertRetryButton() = retryButton.assertIsDisplayed()
}
