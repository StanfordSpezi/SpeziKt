package edu.stanford.bdh.engagehf.bluetooth.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import edu.stanford.spezi.core.coroutines.di.Dispatching
import edu.stanford.spezi.core.logging.speziLogger
import edu.stanford.spezi.module.onboarding.invitation.await
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import org.hl7.fhir.r4.model.Observation
import javax.inject.Inject

internal class ObservationRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth,
    @Dispatching.IO private val ioDispatcher: CoroutineDispatcher,
) {
    private val logger by speziLogger()

    private val heartRateCollection = ObservationCollection("heartRateObservations", "8867-4")

    private val bodyWeightObservation = ObservationCollection("bodyWeightObservations", "29463-7")

    private val bloodPressureCollection =
        ObservationCollection("bloodPressureObservations", "85354-9")

    suspend fun saveObservations(observation: List<Observation>) {
        withContext(ioDispatcher) {
            runCatching {
                firebaseAuth.currentUser?.uid.let { uid ->
                    observation.forEach {
                        if (it.code.coding.any { coding -> coding.code == bodyWeightObservation.code }) {
                            firestore.collection("users/$uid/${bodyWeightObservation.name}").add(it)
                                .await()
                        }
                        if (it.code.coding.any { coding -> coding.code == bloodPressureCollection.code }) {
                            firestore.collection("users/$uid/${bloodPressureCollection.name}")
                                .add(it)
                                .await()
                        }
                        if (it.code.coding.any { coding -> coding.code == heartRateCollection.code }) {
                            firestore.collection("users/$uid/${heartRateCollection.name}").add(it)
                                .await()
                        }
                    }
                }
            }.onFailure {
                logger.e(it) { "Error while saving observation" }
            }
        }
    }
}

data class ObservationCollection(val name: String, val code: String)
