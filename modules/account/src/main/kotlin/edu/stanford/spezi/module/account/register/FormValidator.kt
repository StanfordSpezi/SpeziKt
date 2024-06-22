package edu.stanford.spezi.module.account.register

import androidx.core.util.PatternsCompat

internal abstract class FormValidator {

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

        fun errorMessageOrNull() = if (this is Invalid) message else null

        val isValid: Boolean
            get() = this is Valid
    }
}
