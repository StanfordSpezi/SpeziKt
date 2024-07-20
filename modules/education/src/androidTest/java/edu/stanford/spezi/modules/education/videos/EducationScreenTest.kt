package edu.stanford.spezi.modules.education.videos

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import edu.stanford.spezi.core.design.component.ComposeContentActivity
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class EducationScreenTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComposeContentActivity>()

    @Test
    fun `education screen should display progress bar`() {
        composeTestRule.activity.setScreen {
            EducationScreen(
                uiState = UiState.Loading,
                onAction = {},
            )
        }

        educationScreen {
            assertProgressBar()
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
                uiState = UiState.Error("Could not load videos"),
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
        isExpanded: Boolean = true,
    ): VideoSection = VideoSection(title, description, orderIndex, videos, isExpanded)

    private fun createDefaultEducationUiState(
        videoSections: List<VideoSection> = listOf(createDefaultVideoSection()),
    ): EducationUiState = EducationUiState(videoSections)
}
