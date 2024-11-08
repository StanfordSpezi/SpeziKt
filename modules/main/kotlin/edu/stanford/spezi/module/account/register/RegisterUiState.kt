package edu.stanford.spezi.module.account.register

data class RegisterUiState(
    val email: FieldState = FieldState(),
    val password: FieldState = FieldState(),
    val isFormValid: Boolean = false,
    val isPasswordVisible: Boolean = false,
    val isRegisterButtonEnabled: Boolean = false,
)

data class FieldState(
    val value: String = "",
    val error: String? = null,
)

enum class TextFieldType {
    EMAIL,
    PASSWORD,
}

sealed interface Action {
    data class TextFieldUpdate(val newValue: String, val type: TextFieldType) : Action
    data object OnRegisterPressed : Action
    data object TogglePasswordVisibility : Action
}
