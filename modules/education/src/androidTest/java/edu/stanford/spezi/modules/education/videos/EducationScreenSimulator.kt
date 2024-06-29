package edu.stanford.spezi.modules.education.videos

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.ComposeTestRule
import edu.stanford.spezi.core.testing.onNodeWithIdentifier

class EducationScreenSimulator(composeTestRule: ComposeTestRule) {


    private val videoSection =
        composeTestRule.onNodeWithIdentifier(EducationScreenTestIdentifier.VIDEO_SECTION)

    private val retryButton =
        composeTestRule.onNodeWithIdentifier(EducationScreenTestIdentifier.RETRY_BUTTON)

    private val progressBar =
        composeTestRule.onNodeWithIdentifier(EducationScreenTestIdentifier.PROGRESS_BAR)

    fun assertProgressBar() = this.progressBar.assertIsDisplayed()

    fun assertVideoSection() = this.videoSection.assertIsDisplayed()

    fun assertRetryButton() = this.retryButton.assertIsDisplayed()

}

