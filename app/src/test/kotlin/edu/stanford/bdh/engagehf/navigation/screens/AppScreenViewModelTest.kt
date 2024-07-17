package edu.stanford.bdh.engagehf.navigation.screens

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class AppScreenViewModelTest {
    private val viewModel = AppScreenViewModel()

    @Test
    fun `it should reflect the correct initial state`() {
        // when
        val uiState = viewModel.uiState.value

        // then
        assertThat(uiState.items).isEqualTo(BottomBarItem.entries)
        assertThat(uiState.selectedItem).isEqualTo(BottomBarItem.HOME)
    }

    @Test
    fun `it should handle update action correctly`() {
        // given
        BottomBarItem.entries.forEach { item ->

            // when
            viewModel.onAction(Action.UpdateSelectedBottomBarItem(selectedBottomBarItem = item))

            // then
            assertThat(viewModel.uiState.value.selectedItem).isEqualTo(item)
        }
    }
}
