package edu.stanford.spezi.module.account.register

import javax.inject.Inject

internal class RegisterFormValidator @Inject constructor() : FormValidator() {

    fun firstnameResult(firstName: String): Result =
        if (firstName.isNotEmpty()) Result.Valid else Result.Invalid("First name cannot be empty")

    fun lastnameResult(lastName: String): Result =
        if (lastName.isNotEmpty()) Result.Valid else Result.Invalid("Last name cannot be empty")

    fun isFormValid(uiState: RegisterUiState): Boolean {
        val passwordConditionSatisfied = {
                isValidPassword(uiState.password.value).isValid
        }
        return isValidEmail(uiState.email.value).isValid &&
            passwordConditionSatisfied()
    }
}
