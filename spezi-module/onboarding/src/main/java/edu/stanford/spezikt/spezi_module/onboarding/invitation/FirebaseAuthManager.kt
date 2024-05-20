package edu.stanford.spezikt.spezi_module.onboarding.invitation

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.functions.FirebaseFunctions
import edu.stanford.spezi.logging.speziLogger
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resumeWithException

class FirebaseAuthManager @Inject constructor() {

    private val logger by speziLogger()

    private val functions: FirebaseFunctions by lazy {
        val instance = FirebaseFunctions.getInstance()
        instance.useEmulator("10.0.2.2", 5001)
        instance
    }

    private val auth: FirebaseAuth by lazy {
        val instance = FirebaseAuth.getInstance()
        instance.useEmulator("10.0.2.2", 9099)
        instance
    }

    suspend fun checkInvitationCode(invitationCode: String): Result<Unit> {
        return try {
            auth.signOut()
            val authResult = auth.signInAnonymously().await()
            val userId = authResult.user?.uid

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
        } catch (e: Exception) {
            logger.e { "Failed to check invitation code: ${e.message}" }
            Result.failure(e)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun <T> com.google.android.gms.tasks.Task<T>.await(): T {
        return suspendCancellableCoroutine { cont ->
            addOnSuccessListener { result -> cont.resume(result) { } }
            addOnFailureListener { exception -> cont.resumeWithException(exception) }
        }
    }
}