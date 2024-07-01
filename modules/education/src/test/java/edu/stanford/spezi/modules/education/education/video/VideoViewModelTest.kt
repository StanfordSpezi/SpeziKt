package edu.stanford.spezi.modules.education.education.video

import edu.stanford.spezi.core.navigation.Navigator
import edu.stanford.spezi.modules.education.EducationNavigationEvent
import edu.stanford.spezi.modules.education.video.Action
import edu.stanford.spezi.modules.education.video.VideoViewModel
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Test

class VideoViewModelTest {

    private val navigator: Navigator = mockk(relaxed = true)
    private var viewModel: VideoViewModel = VideoViewModel(navigator)

    @Test
    fun `when back pressed, navigate to pop up`() {
        // Given
        every { navigator.navigateTo(EducationNavigationEvent.PopUp) } returns Unit

        // When
        viewModel.onAction(Action.BackPressed)

        // Then
        verify { navigator.navigateTo(EducationNavigationEvent.PopUp) }
    }
}
