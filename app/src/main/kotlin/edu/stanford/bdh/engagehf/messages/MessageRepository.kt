package edu.stanford.bdh.engagehf.messages

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import edu.stanford.spezi.core.coroutines.di.Dispatching
import edu.stanford.spezi.core.logging.speziLogger
import edu.stanford.spezi.module.account.manager.UserSessionManager
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.withContext
import java.time.ZonedDateTime
import javax.inject.Inject

internal class MessageRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val userSessionManager: UserSessionManager,
    private val firestoreMessageMapper: FirestoreMessageMapper,
    @Dispatching.IO private val ioDispatcher: CoroutineDispatcher,
) {
    private val logger by speziLogger()

    suspend fun observeUserMessages(): Flow<List<Message>> = callbackFlow {
        var listenerRegistration: ListenerRegistration? = null
        withContext(ioDispatcher) {
            runCatching {
                val uid = userSessionManager.getUserUid()
                    ?: throw IllegalStateException("User not authenticated")
                listenerRegistration = firestore.collection("users")
                    .document(uid)
                    .collection("messages")
                    .whereEqualTo("completionDate", null)
                    .addSnapshotListener { value, error ->
                        if (error != null) {
                            logger.e(error) { "Error listening for user messages" }
                            return@addSnapshotListener
                        }
                        value?.documents?.mapNotNull { document ->
                            firestoreMessageMapper.map(document)
                        }?.let { trySend(it) }
                    }
            }.onFailure {
                logger.e(it) { "Error while listening for user messages" }
            }
        }
        awaitClose {
            listenerRegistration?.remove()
            channel.close()
        }
    }

    suspend fun completeMessage(messageId: String) {
        withContext(ioDispatcher) {
            runCatching {
                val uid = userSessionManager.getUserUid()
                    ?: throw IllegalStateException("User not authenticated")
                firestore.collection("users")
                    .document(uid)
                    .collection("messages")
                    .document(messageId)
                    .update("completionDate", ZonedDateTime.now())
            }
        }.onFailure {
            logger.e(it) { "Error while completing message" }
        }
    }
}
