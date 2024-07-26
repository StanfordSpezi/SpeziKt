package edu.stanford.spezi.module.account.register

import java.time.LocalDate

data class RegisterUiState(
    val email: FieldState = FieldState(),
    val password: FieldState = FieldState(),
    val firstName: FieldState = FieldState(),
    val lastName: FieldState = FieldState(),
    val selectedGender: FieldState = FieldState(),
    val dateOfBirth: LocalDate? = null,
    val formattedDateOfBirth: String = "",
    val dateOfBirthError: String? = null,
    val isDropdownMenuExpanded: Boolean = false,
    val isDatePickerDialogOpen: Boolean = false,
    val isFormValid: Boolean = false,
    val genderOptions: List<GenderIdentity> = GenderIdentity.entries,
    val isGoogleSignUp: Boolean = false,
    val isPasswordVisible: Boolean = false,
    val isRegisterButtonEnabled: Boolean = false,
)

enum class GenderIdentity(val displayName: String, val databaseName: String) {
    MALE("Male", "male"),
    FEMALE("Female", "female"),
    TRANSGENDER("Transgender", "transgender"),
    NON_BINARY("Non binary", "nonBinary"),
    PREFER_NOT_TO_STATE("Prefer not to state", "preferNotToState"),
    ;

    companion object {
        fun fromDisplayName(displayName: String): GenderIdentity {
            return entries.first { it.displayName == displayName }
        }
    }
}

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
    data object TogglePasswordVisibility : Action
    data class SetIsGoogleSignUp(val isGoogleSignUp: Boolean) : Action
    data class SetIsDatePickerOpen(val isOpen: Boolean) : Action
}
