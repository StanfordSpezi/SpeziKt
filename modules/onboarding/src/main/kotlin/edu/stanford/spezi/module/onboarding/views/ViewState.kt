package edu.stanford.spezi.module.onboarding.views

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import edu.stanford.spezi.core.design.component.StringResource

sealed interface ViewState {
    data object Idle : ViewState
    data object Processing : ViewState
    data class Error(val throwable: Throwable?) : ViewState

    val errorTitle: String
        @Composable @ReadOnlyComposable get() = StringResource("Error").text()

    val errorDescription: String
        @Composable @ReadOnlyComposable get() = if (this is Error) throwable?.localizedMessage ?: "" else ""
}
