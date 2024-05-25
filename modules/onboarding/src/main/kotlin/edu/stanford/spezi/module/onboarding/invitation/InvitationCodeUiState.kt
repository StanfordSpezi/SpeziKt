package edu.stanford.spezi.module.onboarding.invitation

data class InvitationCodeUiState(
    val invitationCode: String = "",
    val error: String? = ""
)

enum class TextFieldType {
    INVITATION_CODE
}

sealed interface Action {
    data class UpdateInvitationCode(val invitationCode: String, val type: TextFieldType) : Action

    data object ClearError : Action

    data object AlreadyHasAccountPressed : Action

    data object RedeemInvitationCode : Action
}