package edu.stanford.bdh.engagehf.messages

import com.google.common.truth.Truth.assertThat
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.QuerySnapshot
import edu.stanford.spezi.core.testing.runTestUnconfined
import edu.stanford.spezi.module.account.manager.UserSessionManager
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import org.junit.Test

class MessageRepositoryTest {
    private val uid = "some-uid"
    private val firestore: FirebaseFirestore = mockk()
    private val userSessionManager: UserSessionManager = mockk {
        every { getUserUid() } returns uid
    }
    private val firestoreMessageMapper: FirestoreMessageMapper = mockk()

    private val repository = MessageRepository(
        firestore = firestore,
        userSessionManager = userSessionManager,
        firestoreMessageMapper = firestoreMessageMapper,
        ioDispatcher = UnconfinedTestDispatcher()
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
    fun `completeMessage updates completionDate successfully`() = runTestUnconfined {
        // given
        val messageId = "some-message-id"
        val documentReference: DocumentReference = mockk()
        coEvery { documentReference.update("completionDate", any()) } returns mockk()
        every {
            firestore.collection("users")
                .document(uid)
                .collection("messages")
                .document(messageId)
        } returns documentReference


        // when
        repository.completeMessage(messageId)

        // then
        coVerify { documentReference.update("completionDate", any()) }
    }
}
