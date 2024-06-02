package edu.stanford.spezi.app.onboarding

import edu.stanford.spezi.core.navigation.DefaultNavigationEvent
import edu.stanford.spezi.module.onboarding.invitation.InvitationCodeRepository
import edu.stanford.spezi.module.onboarding.invitation.InvitationCodeScreenInfo
import javax.inject.Inject

class DefaultInvitationCodeRepository @Inject constructor() : InvitationCodeRepository {

    override fun getScreenInfo(): InvitationCodeScreenInfo {
        return InvitationCodeScreenInfo(
            title = "Invitation Code",
            description = "Please enter your invitation code to join the ENGAGE-HF study.",
            redeemButtonDefaultNavigationEvent = DefaultNavigationEvent.RegisterScreen,
            alreadyHaveAnAccountDefaultNavigationEvent = DefaultNavigationEvent.LoginScreen
        )
    }
}