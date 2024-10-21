package edu.stanford.bdh.engagehf.contact.data

import com.google.common.truth.Truth.assertThat
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import edu.stanford.spezi.module.account.manager.UserSessionManager
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test

class EngageContactRepositoryTest {
    private val firebaseFirestore: FirebaseFirestore = mockk()
    private val userSessionManager: UserSessionManager = mockk()
    private val contactDocumentToContactMapper: ContactDocumentToContactMapper = mockk()

    private val repository = EngageContactRepository(
        firebaseFirestore = firebaseFirestore,
        userSessionManager = userSessionManager,
        contactDocumentToContactMapper = contactDocumentToContactMapper,
    )

    @Test
    fun `it should return failure if user UID is missing`() = runTest {
        // given
        every { userSessionManager.getUserUid() } returns null

        // when
        val result = repository.getContact()

        // then
        assertThat(result.isFailure).isTrue()
    }

    @Test
    fun `it should return failure if organization is missing`() = runTest {
        // given
        val uid = "some-uid"
        every { userSessionManager.getUserUid() } returns uid
        val userDocument: DocumentReference = mockk {}
        every { firebaseFirestore.collection("users").document(uid) } returns userDocument

        // when
        val result = repository.getContact()

        // then
        assertThat(result.isFailure).isTrue()
    }
}
