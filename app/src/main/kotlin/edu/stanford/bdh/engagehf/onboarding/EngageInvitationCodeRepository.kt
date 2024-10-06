package edu.stanford.bdh.engagehf.onboarding

import edu.stanford.bdh.engagehf.navigation.AppNavigationEvent
import edu.stanford.spezi.core.navigation.Navigator
import edu.stanford.spezi.module.onboarding.invitation.InvitationCodeRepository
import edu.stanford.spezi.module.onboarding.invitation.InvitationCodeScreenData
import javax.inject.Inject

// TODO: Clarify / unify repositories or content provider apis
//      1. Single data object with lambda properties - reference EngageInvitationCodeRepository
//      2. One method to return static content, and additional methods to be invoked for an action
//      reference EngageConsentManager.kt
class EngageInvitationCodeRepository @Inject constructor(
    private val navigator: Navigator,
) : InvitationCodeRepository {

    override fun getScreenData(): InvitationCodeScreenData {
        return InvitationCodeScreenData(
            title = "Invitation Code",
            description = "Please enter your invitation code to join the ENGAGE-HF study.",
            redeemAction = { navigator.navigateTo(AppNavigationEvent.AppScreen(true)) },
        )
    }
}
