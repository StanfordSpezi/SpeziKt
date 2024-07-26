package edu.stanford.spezi.module.account.register

import java.time.LocalDate
import javax.inject.Inject

internal class RegisterFormValidator @Inject constructor() : FormValidator() {

    fun firstnameResult(firstName: String): Result =
        if (firstName.isNotEmpty()) Result.Valid else Result.Invalid("First name cannot be empty")

    fun lastnameResult(lastName: String): Result =
        if (lastName.isNotEmpty()) Result.Valid else Result.Invalid("Last name cannot be empty")

    fun isGenderValid(gender: String): Result =
        if (GenderIdentity.entries.map { it.displayName }.contains(gender)
        ) {
            Result.Valid
        } else {
            Result.Invalid("Please select valid gender")
        }

    fun birthdayResult(dateOfBirth: LocalDate?): Result =
        if (dateOfBirth != null && dateOfBirth.isBefore(LocalDate.now())) {
            Result.Valid
        } else {
            Result.Invalid("Please select valid date of birth")
        }

    fun isFormValid(uiState: RegisterUiState): Boolean {
        val passwordConditionSatisfied = {
            if (uiState.isGoogleSignUp) {
                isValidPassword(uiState.password.value).isValid
            } else {
                true
            }
        }
        return isValidEmail(uiState.email.value).isValid &&
            firstnameResult(uiState.firstName.value).isValid &&
            lastnameResult(uiState.lastName.value).isValid &&
            isGenderValid(uiState.selectedGender.value).isValid &&
            birthdayResult(uiState.dateOfBirth).isValid &&
            passwordConditionSatisfied()
    }
}
