package edu.stanford.spezi.modules.account.manager

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
     * @param phoneNumbers List of verified phone numbers of the user
     */
    data class Registered(
        val hasInvitationCodeConfirmed: Boolean,
        val disabled: Boolean,
        val phoneNumbers: List<String>,
    ) : UserState
}
