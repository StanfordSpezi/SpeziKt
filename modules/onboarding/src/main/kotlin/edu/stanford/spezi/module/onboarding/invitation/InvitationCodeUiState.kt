package edu.stanford.spezi.module.onboarding.invitation

data class InvitationCodeUiState(
    val title: String = "",
    val description: String = "",
    val invitationCode: String = "",
    val error: String? = ""
)

sealed interface Action {
    data class UpdateInvitationCode(val invitationCode: String) : Action

    data object ClearError : Action

    data object AlreadyHasAccountPressed : Action

    data object RedeemInvitationCode : Action
}