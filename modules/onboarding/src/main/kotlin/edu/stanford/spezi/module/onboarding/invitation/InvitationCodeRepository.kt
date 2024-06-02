package edu.stanford.spezi.module.onboarding.invitation

import edu.stanford.spezi.core.navigation.NavigationEvent

interface InvitationCodeRepository {
    fun getScreenInfo(): InvitationCodeScreenInfo
}

data class InvitationCodeScreenInfo(
    val title: String = "Title",
    val description: String = "description",
    val redeemButtonNavigationEvent: NavigationEvent = NavigationEvent.RegisterScreen,
    val alreadyHaveAnAccountNavigationEvent: NavigationEvent = NavigationEvent.LoginScreen
)