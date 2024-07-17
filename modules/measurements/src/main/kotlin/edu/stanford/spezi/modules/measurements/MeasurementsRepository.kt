package edu.stanford.spezi.modules.measurements

import androidx.health.connect.client.records.BloodPressureRecord
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.records.Record
import androidx.health.connect.client.records.WeightRecord
import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.context.FhirVersionEnum
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import edu.stanford.healthconnectonfhir.Loinc
import edu.stanford.healthconnectonfhir.ObservationToRecordMapper
import edu.stanford.spezi.core.bluetooth.data.model.Measurement
import edu.stanford.spezi.core.coroutines.di.Dispatching
import edu.stanford.spezi.core.logging.speziLogger
import edu.stanford.spezi.module.account.manager.UserSessionManager
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.hl7.fhir.r4.model.Observation
import javax.inject.Inject

class MeasurementsRepository @Inject internal constructor(
    private val firestore: FirebaseFirestore,
    private val userSessionManager: UserSessionManager,
    private val observationToRecordMapper: ObservationToRecordMapper,
    private val measurementToObservationMapper: MeasurementToObservationMapper,
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

    suspend fun save(measurement: Measurement) {
        val observations = measurementToObservationMapper.map(measurement = measurement)
        withContext(ioDispatcher) {
            runCatching {
                val uid = userSessionManager.getUserUid()
                    ?: throw IllegalStateException("User not authenticated")
                val batch = firestore.batch()
                val mapType = object : TypeToken<Map<String, Any>>() {}.type
                observations.forEach { observation ->
                    val collectionName = getCollectionNameForObservation(observation)
                    val json = jsonParser.encodeResourceToString(observation)
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

    private suspend fun <T : Record> listenForLatestObservation(
        collection: ObservationCollection,
        onResult: (Result<T?>) -> Unit,
    ) {
        withContext(ioDispatcher) {
            kotlin.runCatching {
                val uid = userSessionManager.getUserUid()
                    ?: throw IllegalStateException("User not authenticated")
                firestore.collection("users/$uid/${collection.name}")
                    .orderBy("effectiveDateTime", Query.Direction.DESCENDING)
                    .limit(1)
                    .addSnapshotListener { snapshot, error ->
                        if (error != null) {
                            logger.e(error) { "Error listening for latest observation in collection: ${collection.name}" }
                            onResult(Result.failure(error))
                        } else {
                            val document = snapshot?.documents?.firstOrNull()
                            document?.let {
                                val json = gson.toJson(it.data)
                                val observation =
                                    jsonParser.parseResource(Observation::class.java, json)
                                onResult(Result.success(observationToRecordMapper.map(observation)))
                            } ?: onResult(Result.success(null))
                        }
                    }
            }.onFailure {
                logger.e(it) { "Error while listening for latest ${collection.name} observation" }
                onResult(Result.failure(it))
            }
        }
    }

    suspend fun listenForLatestBloodPressureObservation(onResult: (Result<BloodPressureRecord?>) -> Unit) {
        listenForLatestObservation(bloodPressureCollection, onResult)
    }

    suspend fun listenForLatestBodyWeightObservation(onResult: (Result<WeightRecord?>) -> Unit) {
        listenForLatestObservation(bodyWeightObservation, onResult)
    }

    suspend fun listenForLatestHeartRateObservation(onResult: (Result<HeartRateRecord?>) -> Unit) {
        listenForLatestObservation(heartRateCollection, onResult)
    }
}

data class ObservationCollection(val name: String, val code: String)
