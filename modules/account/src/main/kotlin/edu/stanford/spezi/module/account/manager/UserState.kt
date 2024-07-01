package edu.stanford.spezi.module.account.manager

/**
 * Encapsulated possible user states
 */
sealed interface UserState {
    /**
     * User information not received yet. Represents the initial state
     */
    data object NotInitialized : UserState

    /**
     * Indicates an anonymous user state
     */
    data object Anonymous : UserState

    /**
     * Indicates a registered user.
     *
     * @property hasConsented Whether the consent pdf has been submitted or not
     */
    data class Registered(val hasConsented: Boolean) : UserState
}
