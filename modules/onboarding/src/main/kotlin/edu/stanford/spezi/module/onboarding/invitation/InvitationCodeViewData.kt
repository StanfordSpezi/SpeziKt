package edu.stanford.spezi.module.onboarding.invitation

data class InvitationCodeViewData(
    val title: String = "Title",
    val description: String = "description",
    val redeemAction: () -> Unit,
)
