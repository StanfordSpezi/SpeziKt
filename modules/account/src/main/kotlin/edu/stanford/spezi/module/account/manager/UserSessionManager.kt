package edu.stanford.spezi.module.account.manager

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import edu.stanford.spezi.core.coroutines.di.Dispatching
import edu.stanford.spezi.core.logging.speziLogger
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.ByteArrayInputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserSessionManager @Inject constructor(
    private val firebaseStorage: FirebaseStorage,
    private val firebaseAuth: FirebaseAuth,
    @Dispatching.IO private val ioDispatcher: CoroutineDispatcher,
    @Dispatching.IO private val coroutineScope: CoroutineScope,
) {
    private val logger by speziLogger()

    private val _userState = MutableStateFlow<UserState?>(value = null)

    private val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
        val user = firebaseAuth.currentUser
        user?.let {
            val isAnonymous = user.isAnonymous
            coroutineScope.launch {
                val userState = UserState(
                    isAnonymous = isAnonymous,
                    hasConsented = if (isAnonymous) false else hasConsented()
                )
                _userState.update { userState }
            }
        }
    }

    init {
        firebaseAuth.addAuthStateListener(authStateListener)
    }

    suspend fun uploadConsentPdf(pdfBytes: ByteArray): Result<Unit> = withContext(ioDispatcher) {
        runCatching {
            val currentUser = firebaseAuth.currentUser ?: error("User not available")
            val inputStream = ByteArrayInputStream(pdfBytes)
            logger.i { "Uploading file to Firebase Storage" }
            val uploaded = firebaseStorage
                .getReference("users/${currentUser.uid}/signature.pdf")
                .putStream(inputStream)
                .await().task.isSuccessful

            if (uploaded) {
                _userState.update {
                    UserState(
                        isAnonymous = currentUser.isAnonymous,
                        hasConsented = true,
                    )
                }
            } else {
                error("Failed to upload signature.pdf")
            }
        }
    }

    fun observeUserState(): Flow<UserState> = _userState.filterNotNull()

    private suspend fun hasConsented(): Boolean = withContext(ioDispatcher) {
        runCatching {
            val uid = firebaseAuth.uid ?: return@runCatching false
            val reference = firebaseStorage.getReference("users/$uid/signature.pdf")
            reference.metadata.await()
        }.isSuccess
    }
}
