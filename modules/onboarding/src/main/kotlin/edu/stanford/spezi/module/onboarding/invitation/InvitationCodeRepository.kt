package edu.stanford.spezi.module.onboarding.invitation

interface InvitationCodeRepository {
    fun getScreenInfo(): InvitationCodeScreenInfo
}

data class InvitationCodeScreenInfo(
    val title: String = "Title",
    val description: String = "description",
    val redeemAction: () -> Unit,
    val gotAnAccountAction: () -> Unit
)