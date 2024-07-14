package edu.stanford.bdh.engagehf.messages

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import edu.stanford.spezi.core.coroutines.di.Dispatching
import edu.stanford.spezi.core.logging.speziLogger
import edu.stanford.spezi.module.account.manager.UserSessionManager
import kotlinx.coroutines.CoroutineDispatcher
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
    private var messageListener: ListenerRegistration? = null

    suspend fun listenForUserMessages(onMessagesUpdate: (List<Message>) -> Unit) {
        withContext(ioDispatcher) {
            runCatching {
                val uid = userSessionManager.getUserUid()
                    ?: throw IllegalStateException("User not authenticated")
                messageListener = firestore.collection("users")
                    .document(uid)
                    .collection("messages")
                    .whereEqualTo("completionDate", null)
                    .addSnapshotListener { value, error ->
                        if (error != null) {
                            logger.e(error) { "Error listening for user messages" }
                            return@addSnapshotListener
                        }
                        val messages = mutableListOf<Message>()
                        value?.documents?.forEach { document ->
                            firestoreMessageMapper.map(document)?.let {
                                messages.add(it)
                            }
                        }
                        onMessagesUpdate(messages)
                    }
            }
        }.onFailure {
            logger.e(it) { "Error while listening for user messages" }
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

    fun stopListeningForUserMessages() {
        messageListener?.remove()
    }
}
