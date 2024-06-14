package edu.stanford.spezi.module.account.login

import edu.stanford.spezi.module.account.register.FieldState

data class UiState(
    val password: FieldState = FieldState(),
    val email: FieldState = FieldState(),
    val passwordVisibility: Boolean = false,
    val showProgress: Boolean = false,
    val showFilterByAuthorizedAccounts: Boolean = true,
    val isFormValid: Boolean = false,
    val isAlreadyRegistered: Boolean = false,
    val hasAttemptedSubmit: Boolean = false,
)

enum class TextFieldType {
    PASSWORD, EMAIL
}

sealed interface Action {
    data class TextFieldUpdate(val newValue: String, val type: TextFieldType) : Action
    data object TogglePasswordVisibility : Action
    data object NavigateToRegister : Action
    data object GoogleSignInOrSignUp : Action
    data object ForgotPassword : Action
    data object PasswordSignInOrSignUp : Action
    data class SetIsAlreadyRegistered(val isAlreadyRegistered: Boolean) : Action
}
