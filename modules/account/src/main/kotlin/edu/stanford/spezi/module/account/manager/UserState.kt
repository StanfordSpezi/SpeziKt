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
     * @property disabled Whether user has completed the study and the account has been disabled
     */
    data class Registered(
        val hasInvitationCodeConfirmed: Boolean,
        val disabled: Boolean,
    ) : UserState
}
