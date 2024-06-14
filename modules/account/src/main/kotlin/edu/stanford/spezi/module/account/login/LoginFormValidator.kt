package edu.stanford.spezi.module.account.login

import edu.stanford.spezi.module.account.register.FormValidator
import javax.inject.Inject

internal class LoginFormValidator @Inject internal constructor() : FormValidator() {

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

    fun isFormValid(uiState: UiState): Boolean {
        return emailResult(uiState.email.value) is Result.Valid &&
            passwordResult(uiState.password.value) is Result.Valid
    }

    fun isEmailValid(email: String): Boolean = emailResult(email) is Result.Valid
}
