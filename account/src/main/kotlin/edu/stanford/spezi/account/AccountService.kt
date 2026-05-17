package edu.stanford.spezi.account

import edu.stanford.spezi.core.Module

/**
 * The main interface for managing user accounts within the Spezi framework.
 *
 * This service provides functionalities for logging out, deleting accounts, and updating account details.
 */
interface AccountService : Module {
    /**
     * The configuration for this [AccountService], as defined in the Spezi configuration.
     */
    val configuration: AccountServiceConfiguration

    /**
     * Creates a new account using the provided [signupDetails].
     *
     * This method is typically used for email/password based registration, where the
     * required credentials are contained in [signupDetails].
     *
     * Depending on the configured account keys, [signupDetails] usually contains values
     * such as email and password, and may also include additional account information.
     *
     * @param signupDetails The account details to use for registration.
     * @return A [Result] indicating whether the sign-up operation succeeded.
     */
    suspend fun signUp(signupDetails: AccountDetails): Result<Unit>

    /**
     * Logs out the current user, if any is signed in.
     * This will typically involve clearing any stored account details and performing any necessary cleanup.
     *
     * @return A [Result] indicating the success or failure of the logout operation.
     */
    suspend fun logout(): Result<Unit>

    /**
     * Deletes the current user's account, if any is signed in.
     * This will typically involve removing any stored account details and performing any necessary cleanup.
     */
    suspend fun delete(): Result<Unit>

    /**
     * Updates the current user's account details with the provided modifications, if any user is signed in.
     */
    suspend fun updateAccountDetails(modifications: AccountModifications): Result<Unit>

    /**
     * Logs a user into an existing account using the provided [credential].
     *
     * @return A [Result] indicating the success or failure of the login operation.
     */
    suspend fun login(credential: UserIdPasswordCredential): Result<Unit>

    /**
     * Invoked when user forgets their password [userId].
     *
     * Use this api to e.g. send a password reset email to the user.
     *
     * @param userId The user identifier for which to reset the password.
     * @return A [Result] indicating whether the password reset request succeeded.
     */
    suspend fun onPasswordForgotten(userId: String): Result<Unit>

    /**
     * Signs in the user with the provided [provider].
     *
     * The default implementation returns a failure indicating that the provider-based sign-in
     * is not supported by this service. Override in concrete implementations that support
     * federated or third-party identity providers (e.g. Google Sign-In).
     *
     * @param provider The authentication provider to use for sign-in.
     * @return A [Result] indicating the success or failure of the sign-in operation.
     */
    suspend fun signIn(provider: AuthProvider): Result<Unit>
}
