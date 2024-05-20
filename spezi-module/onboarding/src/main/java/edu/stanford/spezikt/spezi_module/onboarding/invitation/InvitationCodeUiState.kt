package edu.stanford.spezikt.spezi_module.onboarding.invitation

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
}