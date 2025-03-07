package edu.stanford.spezi.spezi.ui.model

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import edu.stanford.spezi.spezi.ui.resources.StringResource

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
