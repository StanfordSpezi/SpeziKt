package edu.stanford.spezi.module.account.manager

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.functions.FirebaseFunctions
import edu.stanford.spezi.core.logging.speziLogger
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

internal class FirebaseInvitationAuthManager @Inject constructor(
    private val functions: FirebaseFunctions,
    private val auth: FirebaseAuth,
) : InvitationAuthManager {

    private val logger by speziLogger()

    override suspend fun checkInvitationCode(invitationCode: String): Result<Unit> {
        return runCatching {
            val userId = if (auth.currentUser == null) {
                val authResult = auth.signInAnonymously().await()
                authResult.user?.uid
            } else {
                auth.currentUser?.uid
            }

            val data = hashMapOf(
                "invitationCode" to invitationCode,
                "userId" to userId
            )
            logger.i { "Checking invitation code: $data" }

            functions
                .getHttpsCallable("checkInvitationCode")
                .call(data)
                .await()

            logger.i { "Successfully checked invitation code" }
            Result.success(Unit)
        }.onFailure { e ->
            logger.e { "Failed to check invitation code: ${e.message}" }
        }.getOrNull()?.let {
            Result.success(Unit)
        } ?: Result.failure(Exception("Failed to check invitation code"))
    }
}
