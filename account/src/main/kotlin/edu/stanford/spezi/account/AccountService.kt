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
}
