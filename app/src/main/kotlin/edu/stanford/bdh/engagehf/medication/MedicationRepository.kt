package edu.stanford.bdh.engagehf.medication

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
) {
    private val logger by speziLogger()

    fun getMedicationDetails(): List<MedicationDetails> {
        return medicationDetails
    }

    suspend fun observeMedicationDetails(): Flow<Result<List<MedicationDetails>>> = callbackFlow {
        var listenerRegistration: ListenerRegistration? = null
        withContext(ioDispatcher) {
            runCatching {
                val userId = userSessionManager.getUserUid() ?: error("User not logged in")
                listenerRegistration =
                    firestore.collection("users").document(userId).collection("medications")
                        .addSnapshotListener { snapshot, error ->
                            if (error != null) {
                                logger.e(error) { "Error observing medication details" }
                                trySend(Result.failure(error))
                            } else {
                                val documents = snapshot?.documents?.firstOrNull()
                                documents?.let {
                                    val medicationDetails =
                                        it.toObject(MedicationDetails::class.java)
                                    // TODO: possible to use a custom deserializer here; wait for the implementation of firestore seeding
                                    trySend(Result.success(listOf(medicationDetails!!)))
                                } ?: trySend(Result.success(emptyList()))
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

// TODO once firestore seeding is implemented, remove this hardcoded data
private val medicationDetails = listOf(
    MedicationDetails(
        id = "1",
        title = "Medication 1",
        subtitle = "Subtitle 1",
        description = "Description 1",
        type = MedicationRecommendationType.NO_ACTION_REQUIRED,
        dosageInformation = DosageInformation(
            currentSchedule = listOf(
                DoseSchedule(
                    frequency = 1.0,
                    dosage = listOf(1.0),
                ),
            ),
            minimumSchedule = listOf(
                DoseSchedule(
                    frequency = 1.0,
                    dosage = listOf(1.0),
                ),
            ),
            targetSchedule = listOf(
                DoseSchedule(
                    frequency = 1.0,
                    dosage = listOf(1.0),
                ),
            ),
            unit = "mg",
        )
    ),
    MedicationDetails(
        id = "2",
        title = "Medication 2",
        subtitle = "Subtitle 2",
        description = "Description 2",
        type = MedicationRecommendationType.IMPROVEMENT_AVAILABLE,
        dosageInformation = DosageInformation(
            currentSchedule = listOf(
                DoseSchedule(
                    frequency = 1.0,
                    dosage = listOf(1.0),
                ),
            ),
            minimumSchedule = listOf(
                DoseSchedule(
                    frequency = 1.0,
                    dosage = listOf(1.0),
                ),
            ),
            targetSchedule = listOf(
                DoseSchedule(
                    frequency = 1.0,
                    dosage = listOf(1.0),
                ),
            ),
            unit = "mg",
        )
    ),
    MedicationDetails(
        id = "3",
        title = "Medication 3",
        subtitle = "Subtitle 3",
        description = "Description 3",
        type = MedicationRecommendationType.MORE_LAB_OBSERVATIONS_REQUIRED,
        dosageInformation = DosageInformation(
            currentSchedule = listOf(
                DoseSchedule(
                    frequency = 1.0,
                    dosage = listOf(1.0),
                ),
            ),
            minimumSchedule = listOf(
                DoseSchedule(
                    frequency = 1.0,
                    dosage = listOf(1.0),
                ),
            ),
            targetSchedule = listOf(
                DoseSchedule(
                    frequency = 1.0,
                    dosage = listOf(1.0),
                ),
            ),
            unit = "mg",
        )
    ),
)
