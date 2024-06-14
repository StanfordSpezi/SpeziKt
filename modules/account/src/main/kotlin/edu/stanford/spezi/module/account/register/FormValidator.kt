package edu.stanford.spezi.module.account.register

internal open class FormValidator {
    fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun isValidPassword(password: String): Boolean {
        return password.length >= MIN_PASSWORD_LENGTH
    }

    internal companion object {
        const val MIN_PASSWORD_LENGTH = 6 // Minimum for firebase
    }

    sealed interface Result {
        data object Valid : Result
        data class Invalid(val message: String) : Result

        fun errorMessageOrNull() = if (this is Invalid) message else null
    }
}
