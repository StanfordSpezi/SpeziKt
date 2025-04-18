package edu.stanford.spezi.modules.education.videos

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import edu.stanford.spezi.modules.education.R
import edu.stanford.spezi.ui.StringResource
import edu.stanford.spezi.ui.testing.ComposeContentActivity
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class EducationScreenTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComposeContentActivity>()

    @Test
    fun `education screen should display loading`() {
        composeTestRule.activity.setScreen {
            EducationScreen(
                uiState = UiState.Loading,
                onAction = {},
            )
        }

        educationScreen {
            assertLoading()
        }
    }

    @Test
    fun `education screen should display video section`() {
        composeTestRule.activity.setScreen {
            EducationScreen(
                uiState = UiState.Success(createDefaultEducationUiState()),
                onAction = {},
            )
        }

        educationScreen {
            assertVideoSection()
        }
    }

    @Test
    fun `education screen should display retry button`() {
        composeTestRule.activity.setScreen {
            EducationScreen(
                uiState = UiState.Error(StringResource(R.string.failed_to_load_video_sections)),
                onAction = {},
            )
        }

        educationScreen {
            assertRetryButton()
        }
    }

    private fun educationScreen(block: EducationScreenSimulator.() -> Unit) {
        EducationScreenSimulator(composeTestRule).apply(block)
    }

    private fun createDefaultVideo(
        title: String = "title",
        description: String = "description",
        orderIndex: Int = 0,
        youtubeId: String = "id",
    ): Video = Video(title, description, orderIndex, youtubeId)

    private fun createDefaultVideoSection(
        title: String = "title",
        description: String = "description",
        orderIndex: Int = 0,
        videos: List<Video> = listOf(createDefaultVideo()),
    ): VideoSection = VideoSection(title, description, orderIndex, videos)

    private fun createDefaultEducationUiState(
        videoSections: List<VideoSection> = listOf(createDefaultVideoSection()),
    ): EducationUiState = EducationUiState(videoSections)
}
