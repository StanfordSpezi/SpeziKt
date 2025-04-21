package edu.stanford.bdh.engagehf.onboarding

import edu.stanford.bdh.engagehf.R
import edu.stanford.bdh.engagehf.navigation.AppNavigationEvent
import edu.stanford.spezi.modules.navigation.Navigator
import edu.stanford.spezi.modules.onboarding.invitation.InvitationCodeRepository
import edu.stanford.spezi.modules.onboarding.invitation.InvitationCodeViewData
import edu.stanford.spezi.ui.StringResource
import javax.inject.Inject

// TODO: Clarify / unify repositories or content provider apis
//      1. Single data object with lambda properties - reference EngageInvitationCodeRepository
//      2. One method to return static content, and additional methods to be invoked for an action
//      reference EngageConsentManager.kt
class EngageInvitationCodeRepository @Inject constructor(
    private val navigator: Navigator,
) : InvitationCodeRepository {

    override fun getScreenData(): InvitationCodeViewData {
        return InvitationCodeViewData(
            description = StringResource(R.string.invitation_code_description_message),
            redeemAction = { navigator.navigateTo(AppNavigationEvent.AppScreen(true)) },
        )
    }
}
