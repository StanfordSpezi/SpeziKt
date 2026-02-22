package edu.stanford.spezi.account

import edu.stanford.spezi.core.Module
import kotlinx.coroutines.flow.StateFlow

/**
 * The central [Account] module component.
 */
interface Account : Module {

    /**
     * The [AccountService] of this [Account] as configured in Spezi configuration.
     */
    val service: AccountService

    /**
     * Configured account keys with their requirement configuration
     */
    val configuration: AccountValueConfiguration

    /**
     * The current [AccountDetails] of the user, or null if no user is signed in.
     */
    val details: StateFlow<AccountDetails?>

    /**
     * Whether a user is currently signed in, i.e. whether [details] is not null.
     */
    val isSignedIn: Boolean
        get() = details.value != null

    /**
     * Supplies the [Account] with new user details, e.g. after a successful sign in or sign up.
     * The provided [details] must contain an account id under the key specified by [AccountKeys.accountId].
     *
     * This method is intended to be called by the [AccountService] implementation of this [Account]
     * after successful sign in or sign up operations
     */
    fun supplyUserDetails(details: AccountDetails)

    /**
     * Removes the current user details, effectively signing out the user. This method is intended to be called by the [AccountService]
     */
    fun removeUserDetails()
}
