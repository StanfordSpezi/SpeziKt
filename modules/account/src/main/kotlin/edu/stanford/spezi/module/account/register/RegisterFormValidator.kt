package edu.stanford.spezi.module.account.register

import java.time.LocalDate
import javax.inject.Inject

internal class RegisterFormValidator @Inject internal constructor() : FormValidator() {

    fun emailResult(email: String): Result = if (isValidEmail(email)) {
        Result.Valid
    } else {
        Result.Invalid("Invalid email")
    }

    fun passwordResult(password: String): Result = if (isValidPassword(password)) {
        Result.Valid
    } else {
        Result.Invalid("Password must be at least $MIN_PASSWORD_LENGTH characters")
    }

    fun firstnameResult(firstName: String): Result =
        if (firstName.isNotEmpty()) Result.Valid else Result.Invalid("First name cannot be empty")

    fun lastnameResult(lastName: String): Result =
        if (lastName.isNotEmpty()) Result.Valid else Result.Invalid("Last name cannot be empty")

    fun isGenderValid(gender: String): Result =
        if (listOf(
                "Male",
                "Female",
                "Other"
            ).contains(gender)
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
                passwordResult(uiState.password.value) is Result.Valid
            } else {
                true
            }
        }
        return emailResult(uiState.email.value) is Result.Valid &&
            firstnameResult(uiState.firstName.value) is Result.Valid &&
            lastnameResult(uiState.lastName.value) is Result.Valid &&
            isGenderValid(uiState.selectedGender.value) is Result.Valid &&
            birthdayResult(uiState.dateOfBirth) is Result.Valid &&
            passwordConditionSatisfied()
    }
}
