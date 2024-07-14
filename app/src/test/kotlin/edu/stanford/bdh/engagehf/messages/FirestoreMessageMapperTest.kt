package edu.stanford.bdh.engagehf.messages

import com.google.common.truth.Truth.assertThat
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import io.mockk.every
import io.mockk.mockk
import org.junit.Before
import org.junit.Test
import java.time.ZonedDateTime

class FirestoreMessageMapperTest {
    private lateinit var documentSnapshot: DocumentSnapshot
    private lateinit var mapper: FirestoreMessageMapper

    @Before
    fun setUp() {
        documentSnapshot = mockk()
        mapper = FirestoreMessageMapper()
    }

    @Test
    fun `map - when document has all required fields, returns Message`() {
        // Given
        val expectedId = "messageId"
        val expectedDueDate = ZonedDateTime.now()
        val expectedType = MessageType.MedicationChange
        val expectedTitle = "Medication Reminder"
        val expectedDescription = "Time to take your medication"
        val expectedAction = "/medication/1234"

        val documentData = hashMapOf(
            "id" to expectedId,
            "dueDate" to Timestamp(expectedDueDate.toInstant()),
            "type" to expectedType.name,
            "title" to expectedTitle,
            "description" to expectedDescription,
            "action" to expectedAction
        )

        every { documentSnapshot.id } returns expectedId
        every { documentSnapshot.data } returns documentData as Map<String, Any>?

        // When
        val result = mapper.map(documentSnapshot)

        // Then
        assertThat(result).isNotNull()
        assertThat(result?.id).isEqualTo(expectedId)
        assertThat(result?.type).isEqualTo(expectedType)
        assertThat(result?.title).isEqualTo(expectedTitle)
        assertThat(result?.description).isEqualTo(expectedDescription)
        assertThat(result?.action).isEqualTo(expectedAction)
        assertThat(result?.dueDate?.toInstant()).isGreaterThan(
            expectedDueDate.toInstant().minusMillis(1L)
        )
        assertThat(result?.dueDate?.toInstant()).isLessThan(
            expectedDueDate.toInstant().plusMillis(1L)
        )
    }
}
