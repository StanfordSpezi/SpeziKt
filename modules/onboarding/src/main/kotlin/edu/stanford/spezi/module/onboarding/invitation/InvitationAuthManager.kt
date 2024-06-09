package edu.stanford.spezi.module.onboarding.invitation

interface InvitationAuthManager {
    suspend fun checkInvitationCode(invitationCode: String): Result<Unit>
}
