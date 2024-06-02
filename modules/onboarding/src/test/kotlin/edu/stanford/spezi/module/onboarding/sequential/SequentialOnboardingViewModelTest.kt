package edu.stanford.spezi.module.onboarding.sequential

import com.google.common.truth.Truth.assertThat
import edu.stanford.spezi.core.navigation.Navigator
import edu.stanford.spezi.core.testing.CoroutineTestRule
import edu.stanford.spezi.core.testing.runTestUnconfined
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class SequentialOnboardingViewModelTest {
    private val navigator: Navigator = mockk(relaxed = true)
    private val repository: SequentialOnboardingRepository = mockk(relaxed = true)

    @get:Rule
    val coroutineTestRule = CoroutineTestRule()

    private lateinit var sequentialOnboardingViewModel: SequentialOnboardingViewModel

    @Before
    fun setup() = runBlocking {
        coEvery { repository.getSequentialOnboardingData().steps } returns listOf(
            Step(title = "title", description = "description", icon = 0),
            Step(title = "title", description = "description", icon = 0)
        )

        sequentialOnboardingViewModel =
            SequentialOnboardingViewModelFactory.create(repository)
    }

    @Test
    fun `it should fetch steps on init`() = runTestUnconfined {
        // when
        val uiState = sequentialOnboardingViewModel.uiState.first()

        // then
        verify { runBlocking { repository.getSequentialOnboardingData().steps } }
        assertThat(uiState.steps).isNotEmpty()
    }

    @Test
    fun `it should start with first page`() = runTestUnconfined {
        // when
        val uiState = sequentialOnboardingViewModel.uiState.first()

        // then
        assertThat(uiState.currentPage).isEqualTo(0)
    }

    @Test
    fun `it should update current page correctly`() = runTestUnconfined {
        // given
        val action = Action.UpdatePage(ButtonEvent.FORWARD)

        // when
        sequentialOnboardingViewModel.onAction(action)
        val uiState = sequentialOnboardingViewModel.uiState.first()

        // then
        assertThat(uiState.currentPage).isEqualTo(1)
    }
}
