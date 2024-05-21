package edu.stanford.spezikt.spezi_module.onboarding.invitation

interface InvitationAuthManager {
    suspend fun checkInvitationCode(invitationCode: String): Result<Unit>
}
