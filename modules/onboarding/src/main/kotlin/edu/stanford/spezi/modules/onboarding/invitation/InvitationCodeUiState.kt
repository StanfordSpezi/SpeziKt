package edu.stanford.spezi.modules.onboarding.invitation

import edu.stanford.spezi.ui.StringResource

data class InvitationCodeUiState(
    val description: StringResource = StringResource(""),
    val invitationCode: String = "",
    val error: StringResource? = null,
)

sealed interface Action {
    data class UpdateInvitationCode(val invitationCode: String) : Action

    data object ClearError : Action

    data object RedeemInvitationCode : Action
}
