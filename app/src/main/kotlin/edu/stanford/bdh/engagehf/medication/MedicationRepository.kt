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

    private fun getMedicationDetails(): List<MedicationDetails> {
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
                                val documents =
                                    snapshot?.documents?.mapNotNull { it.toObject(MedicationDetails::class.java) }
                                        // TODO Check mapping once firestore seeding is implemented
                                        // TODO Remove custom Seeding Data as well
                                        ?.toList()
                                trySend(Result.success(getMedicationDetails() ?: emptyList()))
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
        title = "Sacubitril/Valsartan",
        subtitle = "ARNI",
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
        title = "Empagliflozin",
        subtitle = "SGLT2i",
        description = "Description 2",
        type = MedicationRecommendationType.NOT_STARTED,
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
    MedicationDetails(
        id = "4",
        title = "Medication 4",
        subtitle = "Subtitle 4",
        description = "Description 4",
        type = MedicationRecommendationType.MORE_PATIENT_OBSERVATIONS_REQUIRED,
        dosageInformation = null
    ),
    MedicationDetails(
        id = "5",
        title = "Medication 5",
        subtitle = "Subtitle 5",
        description = "Description 5",
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
        id = "6",
        title = "Medication 6",
        subtitle = "Subtitle 6",
        description = "Description 6",
        type = MedicationRecommendationType.PERSONAL_TARGET_DOSE_REACHED,
        dosageInformation = DosageInformation(
            currentSchedule = listOf(
                DoseSchedule(
                    frequency = 1.0,
                    dosage = listOf(1.0),
                ),
                DoseSchedule(
                    frequency = 2.0,
                    dosage = listOf(2.0)
                )
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
                DoseSchedule(
                    frequency = 1.0,
                    dosage = listOf(1.0),
                )
            ),
            unit = "mg",
        )
    ),
    MedicationDetails(
        id = "7",
        title = "Carvedilol",
        subtitle = "Beta Blocker",
        description = "Description 7",
        type = MedicationRecommendationType.TARGET_DOSE_REACHED,
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
