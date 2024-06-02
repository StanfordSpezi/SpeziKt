package edu.stanford.spezi.module.onboarding.invitation

import edu.stanford.spezi.core.navigation.DefaultNavigationEvent

interface InvitationCodeRepository {
    fun getScreenInfo(): InvitationCodeScreenInfo
}

data class InvitationCodeScreenInfo(
    val title: String = "Title",
    val description: String = "description",
    val redeemButtonDefaultNavigationEvent: DefaultNavigationEvent = DefaultNavigationEvent.RegisterScreen,
    val alreadyHaveAnAccountDefaultNavigationEvent: DefaultNavigationEvent = DefaultNavigationEvent.LoginScreen
)