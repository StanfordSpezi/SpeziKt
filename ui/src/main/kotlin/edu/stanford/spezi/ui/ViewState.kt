package edu.stanford.spezi.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf

val LocalDefaultErrorTitle = compositionLocalOf { StringResource(R.string.viewstate_default_error_title) }
val LocalDefaultErrorMessage = compositionLocalOf { StringResource(R.string.viewstate_default_error_message) }

sealed interface ViewState {
    data object Idle : ViewState
    data object Processing : ViewState
    data class Error(val throwable: Throwable?) : ViewState {
        val errorTitle: String
            @Composable @ReadOnlyComposable get() = LocalDefaultErrorTitle.current.text()

        val errorMessage: String
            @Composable @ReadOnlyComposable get() = throwable?.localizedMessage ?: LocalDefaultErrorMessage.current.text()
    }
}
