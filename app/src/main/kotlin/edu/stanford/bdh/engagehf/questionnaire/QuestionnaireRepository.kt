package edu.stanford.bdh.engagehf.questionnaire

import com.google.firebase.firestore.FirebaseFirestore
import edu.stanford.bdh.engagehf.observations.ObservationCollection
import edu.stanford.bdh.engagehf.observations.ObservationCollectionProvider
import edu.stanford.healthconnectonfhir.QuestionnaireDocumentMapper
import edu.stanford.spezi.core.coroutines.di.Dispatching
import edu.stanford.spezi.core.logging.speziLogger
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.QuestionnaireResponse
import javax.inject.Inject

class QuestionnaireRepository @Inject constructor(
    @Dispatching.IO private val ioDispatcher: CoroutineDispatcher,
    private val questionnaireDocumentMapper: QuestionnaireDocumentMapper,
    private val observationCollectionProvider: ObservationCollectionProvider,
    private val firestore: FirebaseFirestore,
) {
    private val logger by speziLogger()

    suspend fun byId(id: String): Result<Questionnaire> {
        return withContext(ioDispatcher) {
            kotlin.runCatching {
                val document = firestore.collection(QUESTIONNAIRE_COLLECTION)
                    .document(id)
                    .get()
                    .await()
                val questionnaire = questionnaireDocumentMapper.map(document)
                Result.success(questionnaire)
            }.getOrElse { exception ->
                logger.e(exception) { "Error fetching questionnaire" }
                Result.failure(exception)
            }
        }
    }

    suspend fun save(questionnaireResponse: QuestionnaireResponse): Result<Unit> {
        return withContext(ioDispatcher) {
            kotlin.runCatching {
                val document = questionnaireDocumentMapper.map(questionnaireResponse)
                observationCollectionProvider.getCollection(ObservationCollection.QUESTIONNAIRE)
                    .add(document)
                    .await()
                Result.success(Unit)
            }.getOrElse { exception ->
                logger.e(exception) { "Error saving questionnaire" }
                Result.failure(exception)
            }
        }
    }

    companion object {
        private const val QUESTIONNAIRE_COLLECTION = "questionnaires"
    }
}
