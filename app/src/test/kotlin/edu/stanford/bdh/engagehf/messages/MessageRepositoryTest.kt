package edu.stanford.bdh.engagehf.messages

import com.google.common.truth.Truth.assertThat
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.HttpsCallableReference
import edu.stanford.spezi.core.testing.SpeziTestScope
import edu.stanford.spezi.core.testing.mockTask
import edu.stanford.spezi.core.testing.runTestUnconfined
import edu.stanford.spezi.core.testing.verifyNever
import edu.stanford.spezi.module.account.manager.UserSessionManager
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.launch
import org.junit.Test

class MessageRepositoryTest {
    private val uid = "some-uid"
    private val firestore: FirebaseFirestore = mockk()
    private val firebaseFunctions: FirebaseFunctions = mockk()
    private val userSessionManager: UserSessionManager = mockk {
        every { getUserUid() } returns uid
    }
    private val firestoreMessageMapper: FirestoreMessageMapper = mockk()

    private val repository = MessageRepository(
        firestore = firestore,
        firebaseFunctions = firebaseFunctions,
        userSessionManager = userSessionManager,
        firestoreMessageMapper = firestoreMessageMapper,
        ioScope = SpeziTestScope()
    )

    @Test
    fun `observeUserMessages emits success when value contains valid data`() = runTestUnconfined {
        // given
        val messageDocument: DocumentSnapshot = mockk()
        val message: Message = mockk()
        val querySnapshot: QuerySnapshot = mockk {
            every { documents } returns listOf(messageDocument)
        }
        val listenerSlot = slot<EventListener<QuerySnapshot>>()
        val registrationListener: ListenerRegistration = mockk(relaxed = true)

        every {
            firestore.collection("users")
                .document(uid)
                .collection("messages")
                .whereEqualTo("completionDate", null)
                .addSnapshotListener(capture(listenerSlot))
        } returns registrationListener

        every { firestoreMessageMapper.map(messageDocument) } returns message
        var collectedMessages: List<Message>? = null

        val job = launch {
            repository.observeUserMessages().collect { messages ->
                collectedMessages = messages
            }
        }

        // when
        listenerSlot.captured.onEvent(querySnapshot, null)

        // then
        assertThat(collectedMessages).isEqualTo(listOf(message))

        job.cancel()
    }

    @Test
    fun `dismissMessage is invoked correctly`() {
        // given
        val messageId = "some-message-id"
        val httpsCallableReference: HttpsCallableReference = mockk()
        val paramsSlot = slot<Map<String, String>>()
        every {
            firebaseFunctions.getHttpsCallable("dismissMessage")
        } returns httpsCallableReference
        every { httpsCallableReference.call(capture(paramsSlot)) } returns mockTask(mockk())

        // when
        repository.completeMessage(messageId)

        // then
        val params = paramsSlot.captured
        assertThat(params["userId"]).isEqualTo(uid)
        assertThat(params["messageId"]).isEqualTo(messageId)
    }

    @Test
    fun `it should not dismissMessage if user is not authenticated`() {
        // given
        val messageId = "some-message-id"
        every { userSessionManager.getUserUid() } returns null

        // when
        repository.completeMessage(messageId)

        // then
        verifyNever { firebaseFunctions.getHttpsCallable("dismissMessage") }
    }
}
