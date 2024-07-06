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
                uiState = createDefaultEducationUiState().copy(loading = true),
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
                uiState = createDefaultEducationUiState(),
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
                uiState = createDefaultEducationUiState().copy(loading = false, error = "error"),
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
    ): Video = Video("video", title, description, orderIndex, youtubeId)

    private fun createDefaultVideoSection(
        title: String = "title",
        description: String = "description",
        orderIndex: Int = 0,
        videos: List<Video> = listOf(createDefaultVideo()),
        isExpanded: Boolean = true,
    ): VideoSection = VideoSection(title, description, orderIndex, videos, isExpanded)

    private fun createDefaultEducationUiState(
        videoSections: List<VideoSection> = listOf(createDefaultVideoSection()),
        loading: Boolean = false,
        error: String? = null,
    ): EducationUiState = EducationUiState(videoSections, loading, error)
}
