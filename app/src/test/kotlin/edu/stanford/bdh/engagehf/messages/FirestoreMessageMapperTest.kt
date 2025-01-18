package edu.stanford.bdh.engagehf.messages

import com.google.common.truth.Truth.assertThat
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import edu.stanford.bdh.engagehf.bluetooth.data.mapper.MessageActionMapper
import edu.stanford.bdh.engagehf.localization.LocalizedMapReader
import io.mockk.every
import io.mockk.mockk
import org.junit.Before
import org.junit.Test
import java.time.ZonedDateTime

class FirestoreMessageMapperTest {
    private lateinit var documentSnapshot: DocumentSnapshot
    private lateinit var mapper: FirestoreMessageMapper
    private val messageActionMapper: MessageActionMapper = mockk()
    private val localizedMapReader: LocalizedMapReader = mockk()

    @Before
    fun setUp() {
        documentSnapshot = mockk()
        mapper = FirestoreMessageMapper(localizedMapReader, messageActionMapper)
    }

    @Test
    fun `map - when document has all required fields, returns Message`() {
        // Given
        val expectedId = "messageId"
        val expectedDueDate = ZonedDateTime.now()
        val expectedTitle = "Medication Reminder"
        val expectedDescription = "Time to take your medication"
        val expectedActionString = "/medications"
        val expectedAction = MessageAction.MedicationsAction
        val expectedIsDismissible = true
        every { localizedMapReader.get("title", any()) } returns expectedTitle
        every { localizedMapReader.get("description", any()) } returns expectedDescription
        every { messageActionMapper.map(expectedActionString) } returns Result.success(expectedAction)

        val documentData = hashMapOf(
            "id" to expectedId,
            "dueDate" to Timestamp(expectedDueDate.toInstant()),
            "title" to expectedTitle,
            "description" to expectedDescription,
            "action" to expectedActionString,
            "isDismissible" to expectedIsDismissible
        )

        every { documentSnapshot.id } returns expectedId
        every { documentSnapshot.data } returns documentData as Map<String, Any>?

        // When
        val result = mapper.map(documentSnapshot)

        // Then
        assertThat(result).isNotNull()
        assertThat(result?.id).isEqualTo(expectedId)
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
