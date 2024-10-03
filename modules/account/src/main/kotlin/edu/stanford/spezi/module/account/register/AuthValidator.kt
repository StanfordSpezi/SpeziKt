package edu.stanford.spezi.module.account.register

import androidx.core.util.PatternsCompat
import javax.inject.Inject

internal class AuthValidator @Inject constructor() {

    fun isFormValid(password: String, email: String): Boolean {
        val passwordConditionSatisfied = {
            isValidPassword(password).isValid
        }
        return isValidEmail(email).isValid &&
            passwordConditionSatisfied()
    }

    fun isValidEmail(email: String): Result {
        return if (PatternsCompat.EMAIL_ADDRESS.matcher(email).matches()) {
            Result.Valid
        } else {
            Result.Invalid("Invalid email")
        }
    }

    fun isValidPassword(password: String): Result {
        return if (password.length >= MIN_PASSWORD_LENGTH) {
            Result.Valid
        } else {
            Result.Invalid("Password must be at least $MIN_PASSWORD_LENGTH characters")
        }
    }

    internal companion object {
        const val MIN_PASSWORD_LENGTH = 6 // Minimum for firebase
    }

    sealed interface Result {
        data object Valid : Result
        data class Invalid(val message: String) : Result

        val isValid: Boolean
            get() = this is Valid

        fun errorMessageOrNull() = if (this is Invalid) message else null
    }
}
