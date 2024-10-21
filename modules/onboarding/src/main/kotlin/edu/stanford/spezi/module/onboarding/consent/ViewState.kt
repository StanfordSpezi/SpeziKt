package edu.stanford.spezi.module.onboarding.consent

sealed interface ViewState {
    data object Idle : ViewState
    data object Processing : ViewState
    data class Error(val throwable: Throwable?) : ViewState
}