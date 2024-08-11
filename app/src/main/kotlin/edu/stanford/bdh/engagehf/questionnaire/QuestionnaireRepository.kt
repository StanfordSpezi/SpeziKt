package edu.stanford.bdh.engagehf.questionnaire

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import edu.stanford.healthconnectonfhir.QuestionnaireDocumentMapper
import edu.stanford.spezi.core.coroutines.di.Dispatching
import edu.stanford.spezi.core.logging.speziLogger
import edu.stanford.spezi.module.account.manager.UserSessionManager
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.withContext
import org.hl7.fhir.r4.model.Questionnaire
import javax.inject.Inject

class QuestionnaireRepository @Inject constructor(
    @Dispatching.IO private val ioDispatcher: CoroutineDispatcher,
    private val questionnaireDocumentMapper: QuestionnaireDocumentMapper,
    private val userSessionManager: UserSessionManager,
    private val firestore: FirebaseFirestore,
) {
    private val logger by speziLogger()

    suspend fun observe(id: String): Flow<Result<Questionnaire>> = callbackFlow {
        val listenerRegistration: ListenerRegistration? = null
        withContext(ioDispatcher) {
            kotlin.runCatching {
                val uid = userSessionManager.getUserUid() ?: error("User not authenticated")
                firestore.collection("questionnaires")
                    .document(id)
                    .addSnapshotListener { snapshot, error ->
                        if (error != null) {
                            logger.e(error) { "Error listening for latest questionnaire" }
                            trySend(Result.failure(error))
                        } else {
                            val questionnaire =
                                snapshot?.let { questionnaireDocumentMapper.map(it) }
                            if (questionnaire != null) {
                                trySend(Result.success(questionnaire))
                            }
                        }
                    }
            }.onFailure {
                logger.e(it) { "Error while listening for questionnaire" }
                trySend(Result.failure(it))
            }
        }
        awaitClose {
            listenerRegistration?.remove()
            channel.close()
        }
    }
}
