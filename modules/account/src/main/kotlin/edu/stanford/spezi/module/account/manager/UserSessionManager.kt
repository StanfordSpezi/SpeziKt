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

data class UserState(
    val isAnonymous: Boolean,
    val hasConsented: Boolean,
)

// TODO; review, emit account event instead
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

    suspend fun uploadConsentPdf(pdfBytes: ByteArray): Result<Unit> =
        withContext(ioDispatcher) {
            runCatching {
                val uploaded = firebaseAuth.uid?.let { uid ->
                    val inputStream = ByteArrayInputStream(pdfBytes)
                    logger.i { "Uploading file to Firebase Storage" }
                    firebaseStorage.getReference("users/$uid/signature.pdf")
                        .putStream(inputStream).await().task.isSuccessful
                } ?: false
                if (uploaded.not()) error("Failed to upload signature.pdf")
                // TODO; update user state flow if uploaded?!
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
