package edu.stanford.bdh.engagehf.medication.data

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import edu.stanford.spezi.core.coroutines.di.Dispatching
import edu.stanford.spezi.core.logging.speziLogger
import edu.stanford.spezi.module.account.manager.UserSessionManager
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class MedicationRepository @Inject constructor(
    private val userSessionManager: UserSessionManager,
    @Dispatching.IO private val ioDispatcher: CoroutineDispatcher,
    private val firestore: FirebaseFirestore,
    private val medicationRecommendationMapper: MedicationRecommendationMapper,
) {
    private val logger by speziLogger()

    suspend fun observeMedicationRecommendations(): Flow<Result<List<MedicationRecommendation>>> =
        callbackFlow {
            var listenerRegistration: ListenerRegistration? = null
            withContext(ioDispatcher) {
                runCatching {
                    val userId = userSessionManager.getUserUid() ?: error("User not logged in")
                    listenerRegistration =
                        firestore.collection("users").document(userId)
                            .collection("medicationRecommendations")
                            .addSnapshotListener { snapshot, error ->
                                if (error != null) {
                                    logger.e(error) { "Error observing medication details" }
                                    trySend(Result.failure(error))
                                } else {
                                    val recommendations = snapshot?.documents?.mapNotNull {
                                        medicationRecommendationMapper.map(it)
                                    }
                                    val result = if (recommendations != null) {
                                        Result.success(recommendations)
                                    } else {
                                        Result.failure(IllegalStateException("Failed to parse medication details"))
                                    }
                                    trySend(result)
                                }
                            }
                }.onFailure {
                    logger.e(it) { "Error observing medication details" }
                    trySend(Result.failure(it))
                }
            }
            awaitClose {
                listenerRegistration?.remove()
                channel.close()
            }
        }
}
