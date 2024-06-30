package edu.stanford.bdh.engagehf.bluetooth.data.repository

import androidx.health.connect.client.records.BloodPressureRecord
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.records.WeightRecord
import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.context.FhirVersionEnum
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import edu.stanford.healthconnectonfhir.Loinc
import edu.stanford.healthconnectonfhir.ObservationToRecordMapper
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
    private val observationToRecordMapper: ObservationToRecordMapper,
    @Dispatching.IO private val ioDispatcher: CoroutineDispatcher,
) {
    private val logger by speziLogger()

    private val heartRateCollection =
        ObservationCollection("heartRateObservations", Loinc.HeartRate.CODE)

    private val bodyWeightObservation =
        ObservationCollection("bodyWeightObservations", Loinc.Weight.CODE)

    private val bloodPressureCollection =
        ObservationCollection("bloodPressureObservations", Loinc.BloodPressure.CODE)

    private val jsonParser by lazy {
        FhirContext.forCached(FhirVersionEnum.R4).newJsonParser()
    }

    private val gson by lazy {
        Gson()
    }

    suspend fun saveObservations(observations: List<Observation>) {
        withContext(ioDispatcher) {
            runCatching {
                val uid = firebaseAuth.currentUser?.uid
                    ?: throw IllegalStateException("User not authenticated")
                val batch = firestore.batch()

                observations.forEach { observation ->
                    val collectionName = getCollectionNameForObservation(observation)
                    val json = jsonParser.encodeResourceToString(observation)
                    val mapType = object : TypeToken<Map<String, Any>>() {}.type
                    val data = gson.fromJson<Map<String, Any>>(json, mapType)

                    val docRef = firestore.collection("users/$uid/$collectionName").document()
                    batch.set(docRef, data)
                }

                batch.commit().await()
            }.onFailure {
                logger.e(it) { "Error while saving observations" }
            }
        }
    }

    private fun getCollectionNameForObservation(observation: Observation): String {
        return when {
            observation.code.coding.any { it.code == bodyWeightObservation.code } -> bodyWeightObservation.name
            observation.code.coding.any { it.code == bloodPressureCollection.code } -> bloodPressureCollection.name
            observation.code.coding.any { it.code == heartRateCollection.code } -> heartRateCollection.name
            else -> {
                logger.w { "Unknown observation code" }
                throw UnsupportedOperationException("Unknown observation code")
            }
        }
    }

    private suspend fun <T> getObservation(collection: ObservationCollection): Result<T?> {
        return withContext(ioDispatcher) {
            runCatching {
                val uid = firebaseAuth.currentUser?.uid
                    ?: throw IllegalStateException("User not authenticated")
                firestore.collection("users/$uid/${collection.name}")
                    .orderBy("effectiveDateTime", Query.Direction.DESCENDING)
                    .limit(1)
                    .get()
                    .await()
                    .documents
                    .firstOrNull()
                    ?.let { document ->
                        val json = document.data
                        val jsonString = gson.toJson(json)
                        jsonParser.parseResource(Observation::class.java, jsonString)
                            .toRecord() as T
                    }
            }.onFailure {
                logger.e(it) { "Error while getting latest ${collection.name} observation" }
                null
            }
        }
    }

    private fun Observation.toRecord(): androidx.health.connect.client.records.Record {
        return observationToRecordMapper.map(this)
    }

    suspend fun getLatestBloodPressureObservation(): Result<BloodPressureRecord?> {
        return getObservation<BloodPressureRecord>(bloodPressureCollection)
    }

    suspend fun getLatestBodyWeightObservation(): Result<WeightRecord?> {
        return getObservation<WeightRecord>(bodyWeightObservation)
    }

    suspend fun getLatestHeartRateObservation(): Result<HeartRateRecord?> {
        return getObservation<HeartRateRecord>(heartRateCollection)
    }
}

data class ObservationCollection(val name: String, val code: String)
