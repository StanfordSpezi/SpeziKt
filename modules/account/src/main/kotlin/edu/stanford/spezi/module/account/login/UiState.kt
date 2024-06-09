package edu.stanford.spezi.module.account.login

data class UiState(
    val password: String = "",
    val email: String = "",
    val passwordVisibility: Boolean = false,
    val showProgress: Boolean = false,
    val showFilterByAuthorizedAccounts: Boolean = true,
    val isAlreadyRegistered: Boolean = false,
)

enum class TextFieldType {
    PASSWORD, EMAIL
}

sealed interface Action {
    data class TextFieldUpdate(val newValue: String, val type: TextFieldType) : Action
    data object TogglePasswordVisibility : Action
    data object NavigateToRegister : Action
    data object GoogleSignIn : Action

    data object GoogleSignUp : Action

    data object ForgotPassword : Action

    data object PasswordCredentialSignIn : Action

    data class SetIsAlreadyRegistered(val isAlreadyRegistered: Boolean) : Action
}
