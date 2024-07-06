package edu.stanford.spezi.modules.education.education.videos

import edu.stanford.spezi.core.navigation.Navigator
import edu.stanford.spezi.core.testing.CoroutineTestRule
import edu.stanford.spezi.core.testing.runTestUnconfined
import edu.stanford.spezi.modules.education.EducationNavigationEvent
import edu.stanford.spezi.modules.education.videos.Action
import edu.stanford.spezi.modules.education.videos.EducationViewModel
import edu.stanford.spezi.modules.education.videos.Video
import edu.stanford.spezi.modules.education.videos.data.repository.EducationRepository
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

    private lateinit var viewModel: EducationViewModel

    @Before
    fun setup() {
        viewModel = EducationViewModel(educationRepository, navigator)
    }

    @Test
    fun `when retry action is performed, loadVideoSections is called`() = runTestUnconfined {
        // Given
        coEvery { educationRepository.getVideoSections() } returns Result.success(emptyList())

        // When
        viewModel.onAction(Action.Retry)

        // Then
        coVerify { educationRepository.getVideoSections() }
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
                        title = title
                    )
                )
            )
        } returns Unit

        // When
        viewModel.onAction(
            Action.VideoSectionClicked(
                video = Video(
                    youtubeId = youtubeId,
                    title = title
                )
            )
        )

        // Then
        verify {
            navigator.navigateTo(
                EducationNavigationEvent.VideoSectionClicked(
                    video = Video(
                        youtubeId = youtubeId,
                        title = title
                    )
                )
            )
        }
    }
}
