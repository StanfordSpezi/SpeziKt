package edu.stanford.bdh.engagehf.contact.ui

import com.google.common.truth.Truth.assertThat
import edu.stanford.bdh.engagehf.contact.data.EngageContactRepository
import edu.stanford.spezi.core.navigation.NavigationEvent
import edu.stanford.spezi.core.navigation.Navigator
import edu.stanford.spezi.core.testing.CoroutineTestRule
import edu.stanford.spezi.modules.contact.model.Contact
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ContactScreenViewModelTest {

    @get:Rule
    val coroutineTestRule = CoroutineTestRule()

    private val engageContactRepository: EngageContactRepository = mockk(relaxed = true)
    private val navigator: Navigator = mockk(relaxed = true)

    private val viewModel by lazy {
        ContactScreenViewModel(
            engageContactRepository = engageContactRepository,
            navigator = navigator,
        )
    }

    @Before
    fun setup() {
        coEvery { engageContactRepository.getContact() } returns Result.success(mockk(relaxed = true))
    }

    @Test
    fun `loadContact should update state on success`() = runTest {
        // Given
        val contact = mockk<Contact>()
        coEvery { engageContactRepository.getContact() } returns Result.success(contact)

        // When

        // Then
        val uiState = viewModel.uiState.first()
        assertThat(uiState).isInstanceOf(ContactScreenViewModel.UiState.ContactLoaded::class.java)
        val loadedState = uiState as ContactScreenViewModel.UiState.ContactLoaded
        assertThat(loadedState.contact).isEqualTo(contact)
    }

    @Test
    fun `loadContact should update state on failure`() = runTest {
        // Given
        val errorMessage = "Failed to load contact"
        coEvery { engageContactRepository.getContact() } returns Result.failure(
            Exception(
                errorMessage
            )
        )

        // When

        // Then
        val uiState = viewModel.uiState.first()
        assertThat(uiState).isInstanceOf(ContactScreenViewModel.UiState.Error::class.java)
        val errorState = uiState as ContactScreenViewModel.UiState.Error
        assertThat(errorState.message).isEqualTo(errorMessage)
    }

    @Test
    fun `onAction Back should navigate back`() {
        // When
        viewModel.onAction(ContactScreenViewModel.Action.Back)

        // Then
        verify { navigator.navigateTo(NavigationEvent.PopBackStack) }
    }
}
