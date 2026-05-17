package edu.stanford.spezi.account

/**
 * Credentials for signing in with a user ID and password.
 *
 * @param userId The user's identifier, e.g. email or username.
 * @param password The user's password.
 */
data class UserIdPasswordCredential(
    val userId: String,
    val password: String,
)
