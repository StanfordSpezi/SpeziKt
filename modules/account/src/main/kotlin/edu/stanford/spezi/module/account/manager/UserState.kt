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
     * Indicates a registered user.
     *
     * @property hasInvitationCodeConfirmed Whether the invitation code has been submitted or not
     */
    data class Registered(val hasInvitationCodeConfirmed: Boolean) : UserState
}
