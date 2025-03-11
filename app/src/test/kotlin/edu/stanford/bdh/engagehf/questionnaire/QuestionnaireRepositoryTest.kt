package edu.stanford.bdh.engagehf.questionnaire

import com.google.common.truth.Truth.assertThat
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import edu.stanford.bdh.engagehf.observations.ObservationCollection
import edu.stanford.bdh.engagehf.observations.ObservationCollectionProvider
import edu.stanford.healthconnectonfhir.QuestionnaireDocumentMapper
import edu.stanford.spezi.modules.testing.mockTask
import edu.stanford.spezi.modules.testing.runTestUnconfined
import io.mockk.every
import io.mockk.mockk
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
    fun `getQuestionnaire returns the mapped document success data`() = runTestUnconfined {
        // given
        val testId = "testId"
        val documentSnapshot: DocumentSnapshot = mockk()
        val questionnaire: Questionnaire = mockk()

        every {
            firestore.collection("questionnaires")
                .document(testId)
                .get()
        } returns mockTask(documentSnapshot)

        every { questionnaireDocumentMapper.map(documentSnapshot) } returns questionnaire

        // when
        val result = repository.getQuestionnaire(testId)

        // then
        assertThat(result.isSuccess).isTrue()
        assertThat(questionnaire).isEqualTo(result.getOrNull())
    }

    @Test
    fun `getQuestionnaire returns failure if firestore throws`() = runTestUnconfined {
        // given
        val testId = "testId"
        val exception = Error("Something went wrong")

        every {
            firestore.collection("questionnaires")
                .document(testId)
                .get()
        } throws exception

        // when
        val result = repository.getQuestionnaire(testId)

        // then
        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()).isEqualTo(exception)
    }

    @Test
    fun `it should handle successful questionnaire saving correctly`() = runTestUnconfined {
        // given
        val questionnaireResponse: QuestionnaireResponse = mockk()
        val document: Map<String, Any> = mockk()
        val collection: CollectionReference = mockk()

        every { questionnaireDocumentMapper.map(questionnaireResponse) } returns document
        every { observationCollectionProvider.getCollection(ObservationCollection.QUESTIONNAIRE) } returns collection
        every { collection.add(document) } returns mockTask(mockk())

        // when
        val result = repository.save(questionnaireResponse)

        // then
        assertThat(result.isSuccess).isTrue()
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
