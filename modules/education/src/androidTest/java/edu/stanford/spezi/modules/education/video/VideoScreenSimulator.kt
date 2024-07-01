package edu.stanford.spezi.modules.education.video

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.ComposeTestRule
import edu.stanford.spezi.core.testing.onNodeWithIdentifier

class VideoScreenSimulator(composeTestRule: ComposeTestRule) {

    private val title = composeTestRule.onNodeWithIdentifier(VideoScreenTestIdentifier.TITLE)

    private val videoPlayer =
        composeTestRule.onNodeWithIdentifier(VideoScreenTestIdentifier.VIDEO_PLAYER)

    fun assertTitle(title: String) = this.title.assertIsDisplayed().assertTextEquals(title)

    fun assertVideoPlayer() = this.videoPlayer.assertIsDisplayed()
}
