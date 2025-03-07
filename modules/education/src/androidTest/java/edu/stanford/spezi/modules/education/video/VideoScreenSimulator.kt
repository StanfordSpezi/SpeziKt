package edu.stanford.spezi.modules.education.video

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.ComposeTestRule
import edu.stanford.spezi.spezi.ui.helpers.testing.onNodeWithIdentifier

class VideoScreenSimulator(composeTestRule: ComposeTestRule) {

    private val title = composeTestRule.onNodeWithIdentifier(VideoScreenTestIdentifier.TITLE)

    private val videoPlayer =
        composeTestRule.onNodeWithIdentifier(VideoScreenTestIdentifier.VIDEO_PLAYER)

    private val description =
        composeTestRule.onNodeWithIdentifier(VideoScreenTestIdentifier.VIDEO_DESCRIPTION)

    fun assertTitle(title: String) = this.title.assertIsDisplayed().assertTextEquals(title)

    fun assertVideoPlayer() = this.videoPlayer.assertIsDisplayed()

    fun assertDescription(description: String) =
        this.description.assertIsDisplayed().assertTextEquals(description)
}
