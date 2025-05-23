package edu.stanford.spezi.modules.account.manager

import com.google.firebase.functions.FirebaseFunctions
import edu.stanford.spezi.core.logging.speziLogger
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

internal class FirebaseInvitationAuthManager @Inject constructor(
    private val functions: FirebaseFunctions,
    private val userSessionManager: UserSessionManager,
) : InvitationAuthManager {

    private val logger by speziLogger()

    override suspend fun checkInvitationCode(invitationCode: String): Result<Unit> {
        return runCatching {
            userSessionManager.getUserUid() ?: error("User not logged in")
            val data = hashMapOf(
                "invitationCode" to invitationCode,
            )
            logger.i { "Checking invitation code: $data" }

            functions
                .getHttpsCallable("enrollUser")
                .call(data)
                .await()

            userSessionManager.forceRefresh()
            logger.i { "Successfully checked invitation code" }
            Result.success(Unit)
        }.onFailure { e ->
            logger.e { "Failed to check invitation code: ${e.message}" }
        }.getOrNull()?.let {
            Result.success(Unit)
        } ?: Result.failure(Exception("Failed to check invitation code"))
    }
}
