package edu.stanford.spezi.modules.account.manager

interface InvitationAuthManager {
    suspend fun checkInvitationCode(invitationCode: String): Result<Unit>
}
