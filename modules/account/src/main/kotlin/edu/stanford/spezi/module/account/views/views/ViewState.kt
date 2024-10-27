package edu.stanford.spezi.module.account.views.views

sealed interface ViewState {
    data object Idle : ViewState
    data object Processing : ViewState
    data class Error(val error: kotlin.Error) : ViewState
}
