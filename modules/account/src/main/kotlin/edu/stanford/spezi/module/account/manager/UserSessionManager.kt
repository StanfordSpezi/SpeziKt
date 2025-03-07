package edu.stanford.spezi.module.account.manager

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.storage.FirebaseStorage
import edu.stanford.spezi.module.account.AccountEvents
import edu.stanford.spezi.spezi.core.logging.coroutines.di.Dispatching
import edu.stanford.spezi.spezi.core.logging.speziLogger
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
    fun observeRegisteredUser(): Flow<UserState.Registered>
    fun getUserUid(): String?
    fun getUserInfo(): UserInfo
    suspend fun forceRefresh()
    fun signOut()
}

@Singleton
internal class UserSessionManagerImpl @Inject constructor(
    private val firebaseStorage: FirebaseStorage,
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth,
    private val accountEvents: AccountEvents,
    @Dispatching.IO private val ioDispatcher: CoroutineDispatcher,
    @Dispatching.IO private val coroutineScope: CoroutineScope,
) : UserSessionManager {
    private val logger by speziLogger()

    override fun signOut() {
        runCatching {
            firebaseAuth.signOut()
        }.onSuccess {
            accountEvents.emit(event = AccountEvents.Event.SignOutSuccess)
        }.onFailure {
            accountEvents.emit(event = AccountEvents.Event.SignOutFailure)
        }
    }

    override suspend fun uploadConsentPdf(pdfBytes: ByteArray): Result<Unit> =
        withContext(ioDispatcher) {
            runCatching {
                val currentUser = firebaseAuth.currentUser ?: error("User not available")
                val inputStream = ByteArrayInputStream(pdfBytes)
                logger.i { "Uploading file to Firebase Storage" }
                val uploaded =
                    firebaseStorage.getReference("users/${currentUser.uid}/consent/consent.pdf")
                        .putStream(inputStream).await().task.isSuccessful

                if (!uploaded) error("Failed to upload consent.pdf")
            }
        }

    override suspend fun getUserState(): UserState {
        val user = firebaseAuth.currentUser
        if (user == null || user.isAnonymous) {
            logger.i { "User is not available" }
            return UserState.NotInitialized
        } else {
            return getRegisteredUserState()
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

    override fun observeRegisteredUser(): Flow<UserState.Registered> {
        return callbackFlow {
            var listenerRegistration: ListenerRegistration? = null
            withContext(ioDispatcher) {
                runCatching {
                    listenerRegistration = userDocument()
                        .addSnapshotListener { snapshot, _ ->
                            trySend(registeredUser(document = snapshot))
                        }
                }.onFailure {
                    logger.e(it) { "Error observing registered user" }
                }
            }
            awaitClose {
                listenerRegistration?.remove()
                channel.close()
            }
        }
    }

    override fun getUserUid(): String? = firebaseAuth.uid

    override fun getUserInfo(): UserInfo {
        val user = firebaseAuth.currentUser
        return UserInfo(
            email = user?.email ?: "",
            name = user?.displayName?.takeIf { it.isNotBlank() },
        )
    }

    override suspend fun forceRefresh() {
        runCatching {
            firebaseAuth.currentUser?.getIdToken(true)?.await()
        }.onFailure {
            logger.e { "Failed to force refresh user" }
        }
    }

    @Suppress("UnusedPrivateMember")
    private suspend fun hasConsented(): Boolean = withContext(ioDispatcher) {
        runCatching {
            val uid = getUserUid() ?: error("No uid available")
            val reference = firebaseStorage.getReference("users/$uid/consent/consent.pdf")
            reference.metadata.await()
        }.isSuccess
    }

    private fun userDocument(): DocumentReference {
        val uid = getUserUid() ?: error("No uid available")
        return firestore.collection(USERS_PATH).document(uid)
    }

    private suspend fun getRegisteredUserState(): UserState.Registered = withContext(ioDispatcher) {
        runCatching {
            val document = userDocument().get().await()
            registeredUser(document = document)
        }.getOrDefault(
            UserState.Registered(
                hasInvitationCodeConfirmed = false,
                disabled = false,
            )
        )
    }

    private fun registeredUser(document: DocumentSnapshot?) = UserState.Registered(
        hasInvitationCodeConfirmed = document?.getString(INVITATION_CODE_KEY) != null,
        disabled = document?.getBoolean(DISABLED_KEY) == true
    )

    private companion object {
        const val USERS_PATH = "users"
        const val INVITATION_CODE_KEY = "invitationCode"
        const val DISABLED_KEY = "disabled"
    }
}
