package edu.stanford.spezi.module.onboarding.invitation

data class InvitationCodeScreenData(
    val title: String = "Title",
    val description: String = "description",
    val redeemAction: () -> Unit,
    val gotAnAccountAction: () -> Unit,
)
