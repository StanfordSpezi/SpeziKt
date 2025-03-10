package edu.stanford.spezi.modules.onboarding.invitation

interface InvitationCodeRepository {
    fun getScreenData(): InvitationCodeViewData
}
