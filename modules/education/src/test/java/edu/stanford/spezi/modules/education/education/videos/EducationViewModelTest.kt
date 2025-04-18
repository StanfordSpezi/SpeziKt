package edu.stanford.spezi.modules.education.education.videos

import com.google.common.truth.Truth.assertThat
import edu.stanford.spezi.modules.education.EducationNavigationEvent
import edu.stanford.spezi.modules.education.videos.Action
import edu.stanford.spezi.modules.education.videos.EducationViewModel
import edu.stanford.spezi.modules.education.videos.UiState
import edu.stanford.spezi.modules.education.videos.Video
import edu.stanford.spezi.modules.education.videos.VideoSection
import edu.stanford.spezi.modules.education.videos.data.repository.EducationRepository
import edu.stanford.spezi.modules.navigation.Navigator
import edu.stanford.spezi.modules.testing.CoroutineTestRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class EducationViewModelTest {

    private val navigator: Navigator = mockk(relaxed = true)
    private val educationRepository: EducationRepository = mockk(relaxed = true)

    @get:Rule
    val coroutineTestRule = CoroutineTestRule()

    private val viewModel by lazy {
        EducationViewModel(educationRepository, navigator)
    }

    @Before
    fun setUp() {
        coEvery { educationRepository.getVideoSections() } returns Result.success(emptyList())
    }

    @Test
    fun `it should sort video sections on the order index`() {
        // given
        val size = 100
        val videoSections = List(size) {
            VideoSection(
                title = "title$it",
                description = "description$it",
                orderIndex = size - it,
            )
        }.reversed()
        coEvery { educationRepository.getVideoSections() } returns Result.success(videoSections)

        // when
        val uiState = viewModel.uiState.value as UiState.Success
        val uiStateVideoSections = uiState.data.videoSections

        // then
        assertThat(uiStateVideoSections).isEqualTo(videoSections.sortedBy { it.orderIndex })
        assertThat(uiStateVideoSections.first().orderIndex).isEqualTo(1)
        assertThat(uiStateVideoSections.last().orderIndex).isEqualTo(size)
    }

    @Test
    fun `when retry action is performed, loadVideoSections is called`() {
        // When
        viewModel.onAction(Action.Retry)

        // Then
        coVerify(exactly = 2) { educationRepository.getVideoSections() }
    }

    @Test
    fun `when VideoSectionClicked action is performed, navigateTo is called`() {
        // Given
        val youtubeId = "testId"
        val title = "testTitle"
        every {
            navigator.navigateTo(
                EducationNavigationEvent.VideoSectionClicked(
                    Video(
                        youtubeId = youtubeId,
                        title = title,
                        description = "description",
                    )
                )
            )
        } returns Unit

        // When
        viewModel.onAction(
            Action.VideoSectionClicked(
                video = Video(
                    youtubeId = youtubeId,
                    title = title,
                    description = "description",
                )
            )
        )

        // Then
        verify {
            navigator.navigateTo(
                EducationNavigationEvent.VideoSectionClicked(
                    video = Video(
                        youtubeId = youtubeId,
                        title = title,
                        description = "description",
                    )
                )
            )
        }
    }
}
