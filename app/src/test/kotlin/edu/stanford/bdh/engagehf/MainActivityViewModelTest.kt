package edu.stanford.bdh.engagehf

import com.google.common.truth.Truth.assertThat
import edu.stanford.spezi.core.navigation.Navigator
import edu.stanford.spezi.core.testing.CoroutineTestRule
import edu.stanford.spezi.core.testing.runTestUnconfined
import edu.stanford.spezi.module.account.AccountEvents
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class MainActivityViewModelTest {

    private var mockAccountEvents: AccountEvents = mockk(relaxed = true)
    private var mockNavigator: Navigator = mockk(relaxed = true)

    private lateinit var viewModel: MainActivityViewModel

    @get:Rule
    val coroutineTestRule = CoroutineTestRule()

    @Before
    fun setup() {
        viewModel = MainActivityViewModel(mockAccountEvents, mockNavigator)
    }

    @Test
    fun `given selectedIndex when onAction UpdateSelectedIndex then uiState selectedIndex should be updated`() =
        runTestUnconfined {
            // Given
            val initialIndex = viewModel.uiState.value.selectedIndex
            val newIndex = initialIndex + 1

            // When
            viewModel.onAction(Action.UpdateSelectedIndex(newIndex))

            // Then
            val updatedIndex = viewModel.uiState.value.selectedIndex
            assertThat(updatedIndex).isEqualTo(newIndex)
        }
}
