package edu.stanford.spezi.module.account.manager

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import edu.stanford.spezi.core.coroutines.di.Dispatching
import edu.stanford.spezi.core.logging.speziLogger
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.ByteArrayInputStream
import javax.inject.Inject
import javax.inject.Singleton

interface UserSessionManager {
    suspend fun uploadConsentPdf(pdfBytes: ByteArray): Result<Unit>
    suspend fun getUserState(): UserState
    fun observeUserState(): Flow<UserState>
    fun getUserUid(): String?
}

@Singleton
internal class UserSessionManagerImpl @Inject constructor(
    private val firebaseStorage: FirebaseStorage,
    private val firebaseAuth: FirebaseAuth,
    @Dispatching.IO private val ioDispatcher: CoroutineDispatcher,
    @Dispatching.IO private val coroutineScope: CoroutineScope,
) : UserSessionManager {
    private val logger by speziLogger()

    override suspend fun uploadConsentPdf(pdfBytes: ByteArray): Result<Unit> =
        withContext(ioDispatcher) {
            runCatching {
                val currentUser = firebaseAuth.currentUser ?: error("User not available")
                val inputStream = ByteArrayInputStream(pdfBytes)
                logger.i { "Uploading file to Firebase Storage" }
                val uploaded = firebaseStorage
                    .getReference("users/${currentUser.uid}/consent.pdf")
                    .putStream(inputStream)
                    .await().task.isSuccessful

                if (!uploaded) error("Failed to upload signature.pdf")
            }
        }

    override suspend fun getUserState(): UserState {
        val user = firebaseAuth.currentUser
        return when {
            user == null -> UserState.NotInitialized
            user.isAnonymous -> UserState.Anonymous
            else -> UserState.Registered(hasConsented = hasConsented())
        }
    }

    override fun observeUserState(): Flow<UserState> = callbackFlow {
        val authStateListener = FirebaseAuth.AuthStateListener { _ ->
            coroutineScope.launch { send(getUserState()) }
        }

        firebaseAuth.addAuthStateListener(authStateListener)
        awaitClose {
            firebaseAuth.removeAuthStateListener(authStateListener)
            channel.close()
        }
    }

    override fun getUserUid(): String? = firebaseAuth.uid

    private suspend fun hasConsented(): Boolean = withContext(ioDispatcher) {
        runCatching {
            val uid = getUserUid() ?: error("No uid available")
            val reference = firebaseStorage.getReference("users/$uid/consent.pdf")
            reference.metadata.await()
        }.isSuccess
    }
}
