package edu.stanford.spezi.module.account.manager

interface InvitationAuthManager {
    suspend fun checkInvitationCode(invitationCode: String): Result<Unit>
}
