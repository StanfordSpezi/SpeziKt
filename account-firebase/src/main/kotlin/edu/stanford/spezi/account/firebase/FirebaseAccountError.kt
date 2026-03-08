package edu.stanford.spezi.account.firebase

import com.google.firebase.auth.FirebaseAuthException

/**
 * Errors that can occur during Firebase account operations.
 */
sealed class FirebaseAccountError : Exception() {

    /**
     * No user is currently signed in, but an operation requiring authentication was attempted.
     */
    class NotSignedIn : FirebaseAccountError()

    /**
     * The provided credentials are invalid, such as an incorrect password or malformed email address.
     */
    class InvalidCredentials : FirebaseAccountError()

    /**
     * The provided password does not meet the security requirements defined by Firebase, such as minimum length or complexity.
     */
    class WeakPassword : FirebaseAccountError()

    /**
     * The email address provided is already associated with an existing account,
     * preventing the creation of a new account with the same email.
     */
    class EmailAlreadyInUse : FirebaseAccountError()

    /**
     * No account exists corresponding to the provided credentials,
     * such as an email address that is not registered or a user ID that does not exist.
     */
    class UserNotFound : FirebaseAccountError()

    /**
     * A network error occurred while attempting to communicate with Firebase, such as loss of connectivity or timeout.
     */
    class NetworkError : FirebaseAccountError()

    /**
     * The attempted authentication method is not allowed or supported by the Firebase configuration.
     */
    class AuthenticationMethodNotAllowed : FirebaseAccountError()

    /**
     * Authentication failed for an unspecified reason, such as an invalid token or expired session.
     */
    class AuthenticationFailed : FirebaseAccountError()

    /**
     * An unknown error occurred during a Firebase account operation that does not fit into the other defined categories.
     */
    data class Unknown(override val cause: Throwable) : FirebaseAccountError()

    internal companion object {

        /**
         * Maps a generic [Throwable] to a specific [FirebaseAccountError] based on its type and properties.
         */
        fun from(throwable: Throwable): FirebaseAccountError {
            if (throwable is FirebaseAccountError) return throwable
            val firebaseException = throwable as? FirebaseAuthException ?: return Unknown(throwable)
            return when (firebaseException.errorCode) {
                "ERROR_INVALID_CREDENTIAL",
                "ERROR_WRONG_PASSWORD",
                "ERROR_INVALID_EMAIL",
                -> InvalidCredentials()

                "ERROR_WEAK_PASSWORD" -> WeakPassword()
                "ERROR_EMAIL_ALREADY_IN_USE" -> EmailAlreadyInUse()
                "ERROR_USER_NOT_FOUND" -> UserNotFound()
                "ERROR_NETWORK_REQUEST_FAILED" -> NetworkError()
                else -> Unknown(throwable)
            }
        }
    }
}
