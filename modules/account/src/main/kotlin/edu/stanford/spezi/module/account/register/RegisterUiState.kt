package edu.stanford.spezi.module.account.register

import java.time.LocalDate

data class RegisterUiState(
    val email: FieldState = FieldState(),
    val password: FieldState = FieldState(),
    val firstName: FieldState = FieldState(),
    val lastName: FieldState = FieldState(),
    val selectedGender: FieldState = FieldState(),
    val dateOfBirth: LocalDate? = null,
    val dateOfBirthError: String? = null,
    val isDropdownMenuExpanded: Boolean = false,
    val isFormValid: Boolean = false,
    val genderOptions: List<String> = listOf("Male", "Female", "Other"),
    val isGoogleSignUp: Boolean = false,
)

data class FieldState(
    val value: String = "",
    val error: String? = null,
)

enum class TextFieldType {
    EMAIL,
    PASSWORD,
    FIRST_NAME,
    LAST_NAME,
    GENDER,
}

sealed interface Action {
    data class TextFieldUpdate(val newValue: String, val type: TextFieldType) : Action
    data class DateFieldUpdate(val newValue: LocalDate) : Action
    data class DropdownMenuExpandedUpdate(val isExpanded: Boolean) : Action
    data object OnRegisterPressed : Action

    data class SetIsGoogleSignUp(val isGoogleSignUp: Boolean) : Action
}
