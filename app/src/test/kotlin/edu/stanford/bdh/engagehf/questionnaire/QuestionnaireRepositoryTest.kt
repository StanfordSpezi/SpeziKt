package edu.stanford.bdh.engagehf.questionnaire

import com.google.common.truth.Truth.assertThat
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import edu.stanford.bdh.engagehf.observations.ObservationCollection
import edu.stanford.bdh.engagehf.observations.ObservationCollectionProvider
import edu.stanford.healthconnectonfhir.QuestionnaireDocumentMapper
import edu.stanford.spezi.core.testing.runTestUnconfined
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.QuestionnaireResponse
import org.junit.Test

class QuestionnaireRepositoryTest {

    private var firestore: FirebaseFirestore = mockk()
    private var questionnaireDocumentMapper: QuestionnaireDocumentMapper = mockk()
    private var observationCollectionProvider: ObservationCollectionProvider = mockk()

    private var repository: QuestionnaireRepository = QuestionnaireRepository(
        firestore = firestore,
        questionnaireDocumentMapper = questionnaireDocumentMapper,
        observationCollectionProvider = observationCollectionProvider,
        ioDispatcher = UnconfinedTestDispatcher()
    )

    @Test
    fun `observe emits success when value contains valid data`() = runTestUnconfined {
        // given
        val testId = "testId"
        val documentSnapshot: DocumentSnapshot = mockk()
        val questionnaire: Questionnaire = mockk()
        val listenerSlot = slot<EventListener<DocumentSnapshot>>()
        val registrationListener: ListenerRegistration = mockk(relaxed = true)

        every {
            firestore.collection("questionnaires")
                .document(testId)
                .addSnapshotListener(capture(listenerSlot))
        } returns registrationListener

        every { questionnaireDocumentMapper.map(documentSnapshot) } returns questionnaire
        var collectedQuestionnaire: Result<Questionnaire>? = null

        val job = launch {
            repository.observe(testId).collect { result ->
                collectedQuestionnaire = result
            }
        }

        // when
        listenerSlot.captured.onEvent(documentSnapshot, null)

        // then
        assertThat(collectedQuestionnaire).isEqualTo(Result.success(questionnaire))

        job.cancel()
    }

    @Test
    fun `save returns failure when an exception is thrown`() = runTest {
        // given
        val questionnaireResponse: QuestionnaireResponse = mockk()
        val document: Map<String, Any> = mockk()
        val exception = Exception("Test exception")

        every { questionnaireDocumentMapper.map(questionnaireResponse) } returns document
        every { observationCollectionProvider.getCollection(ObservationCollection.QUESTIONNAIRE) } throws exception

        // when
        val result = repository.save(questionnaireResponse)

        // then
        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()).isEqualTo(exception)
    }
}
