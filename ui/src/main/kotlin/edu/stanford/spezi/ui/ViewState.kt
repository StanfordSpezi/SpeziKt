package edu.stanford.spezi.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable

sealed interface ViewState {
    data object Idle : ViewState
    data object Processing : ViewState
    data class Error(val throwable: Throwable?) : ViewState {
        val errorTitle: String
            @Composable @ReadOnlyComposable get() = StringResource("Error").text()

        val errorDescription: String
            @Composable @ReadOnlyComposable get() = throwable?.localizedMessage ?: ""
    }
}
