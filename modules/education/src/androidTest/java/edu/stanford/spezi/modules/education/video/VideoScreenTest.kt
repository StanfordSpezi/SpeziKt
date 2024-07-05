package edu.stanford.spezi.modules.education.video

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import edu.stanford.spezi.core.design.component.ComposeContentActivity
import edu.stanford.spezi.modules.education.video.VideoScreenTest.UiStateFactory.createVideoScreenState
import edu.stanford.spezi.modules.education.videos.Video
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class VideoScreenTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComposeContentActivity>()

    private val videoScreenState = createVideoScreenState()

    @Before
    fun setup() {
        composeTestRule.activity.setScreen {
            VideoScreen(
                { },
                Video(
                    youtubeId = videoScreenState.videoId,
                    title = videoScreenState.videoTitle,
                )
            )
        }
    }

    @Test
    fun `video screen should display title`() {
        videoScreen {
            assertTitle(videoScreenState.videoTitle)
        }
    }

    @Test
    fun `video screen should display video player`() {
        videoScreen {
            assertVideoPlayer()
        }
    }

    object UiStateFactory {

        fun createVideoScreenState(
            videoId: String = "videoId",
            videoTitle: String = "videoTitle",
        ): VideoScreenState {
            return VideoScreenState(
                videoId = videoId,
                videoTitle = videoTitle
            )
        }
    }

    data class VideoScreenState(
        val videoId: String?,
        val videoTitle: String,
    )

    private fun videoScreen(block: VideoScreenSimulator.() -> Unit) =
        VideoScreenSimulator(composeTestRule).apply(block)
}
