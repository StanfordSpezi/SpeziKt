package edu.stanford.spezi.module.account.login

import edu.stanford.spezi.core.design.action.PendingActions
import edu.stanford.spezi.module.account.register.FieldState

data class UiState(
    val password: FieldState = FieldState(),
    val email: FieldState = FieldState(),
    val passwordVisibility: Boolean = false,
    val showProgress: Boolean = false,
    val showFilterByAuthorizedAccounts: Boolean = true,
    val isFormValid: Boolean = false,
    val isPasswordSignInEnabled: Boolean = false,
    val pendingActions: PendingActions<Action.Async> = PendingActions(),
)

enum class TextFieldType {
    PASSWORD, EMAIL
}

sealed interface Action {
    data class TextFieldUpdate(val newValue: String, val type: TextFieldType) : Action
    data object TogglePasswordVisibility : Action
    data object NavigateToRegister : Action
    data class EmailClicked(val email: String) : Action

    sealed interface Async : Action {
        data object ForgotPassword : Async
        data object GoogleSignInOrSignUp : Async
        data object PasswordSignIn : Async
    }
}
