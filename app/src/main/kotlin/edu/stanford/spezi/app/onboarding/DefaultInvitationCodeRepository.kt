package edu.stanford.spezi.app.onboarding

import edu.stanford.spezi.core.navigation.Navigator
import edu.stanford.spezi.module.account.AccountNavigationEvent
import edu.stanford.spezi.module.onboarding.invitation.InvitationCodeRepository
import edu.stanford.spezi.module.onboarding.invitation.InvitationCodeScreenInfo
import javax.inject.Inject

class DefaultInvitationCodeRepository @Inject constructor(
    private val navigator: Navigator
) : InvitationCodeRepository {

    override fun getScreenInfo(): InvitationCodeScreenInfo {
        return InvitationCodeScreenInfo(
            title = "Invitation Code",
            description = "Please enter your invitation code to join the ENGAGE-HF study.",
            redeemAction = { navigator.navigateTo(AccountNavigationEvent.RegisterScreen) },
            gotAnAccountAction = { navigator.navigateTo(AccountNavigationEvent.LoginScreen) }
        )
    }
}