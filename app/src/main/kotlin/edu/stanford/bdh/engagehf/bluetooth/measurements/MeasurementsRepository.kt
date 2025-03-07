package edu.stanford.bdh.engagehf.bluetooth.measurements

import androidx.health.connect.client.records.BloodPressureRecord
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.records.Record
import androidx.health.connect.client.records.WeightRecord
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import edu.stanford.bdh.engagehf.bluetooth.service.Measurement
import edu.stanford.bdh.engagehf.observations.ObservationCollection
import edu.stanford.bdh.engagehf.observations.ObservationCollectionProvider
import edu.stanford.healthconnectonfhir.ObservationsDocumentMapper
import edu.stanford.spezi.spezi.core.logging.coroutines.di.Dispatching
import edu.stanford.spezi.spezi.core.logging.speziLogger
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.hl7.fhir.r4.model.Observation
import javax.inject.Inject

class MeasurementsRepository @Inject internal constructor(
    private val firestore: FirebaseFirestore,
    private val observationCollectionProvider: ObservationCollectionProvider,
    private val observationMapper: ObservationsDocumentMapper,
    private val measurementToObservationMapper: MeasurementToObservationMapper,
    @Dispatching.IO private val ioDispatcher: CoroutineDispatcher,
) {
    private val logger by speziLogger()

    suspend fun save(measurement: Measurement) {
        val observations = measurementToObservationMapper.map(measurement = measurement)
        withContext(ioDispatcher) {
            runCatching {
                val batch = firestore.batch()
                observations.forEach { observation ->
                    val collection = getCollectionForObservation(observation)
                    val docRef = observationCollectionProvider.getCollection(collection).document()
                    val data = observationMapper.map(observation = observation)
                    batch.set(docRef, data)
                }

                batch.commit().await()
            }.onFailure {
                logger.e(it) { "Error while saving observations" }
            }
        }
    }

    private fun getCollectionForObservation(observation: Observation): ObservationCollection {
        return ObservationCollection.entries.find { collection ->
            observation.code.coding.any { it.code == collection.loinc?.code }
        } ?: run {
            logger.w { "Unknown observation code" }
            throw UnsupportedOperationException("Unknown observation code")
        }
    }

    private suspend fun <T : Record> observe(
        collection: ObservationCollection,
    ): Flow<Result<T?>> = callbackFlow {
        var listenerRegistration: ListenerRegistration? = null
        withContext(ioDispatcher) {
            kotlin.runCatching {
                listenerRegistration = observationCollectionProvider.getCollection(collection)
                    .orderBy("effectiveDateTime", Query.Direction.DESCENDING)
                    .limit(1)
                    .addSnapshotListener { snapshot, error ->
                        if (error != null) {
                            logger.e(error) { "Error listening for latest observation in collection: ${collection.name}" }
                            trySend(Result.failure(error))
                        } else {
                            val document = snapshot?.documents?.firstOrNull()
                            document?.let {
                                trySend(Result.success(observationMapper.map(it)))
                            } ?: trySend(Result.success(null))
                        }
                    }
            }.onFailure {
                logger.e(it) { "Error while listening for latest ${collection.name} observation" }
                trySend(Result.failure(it))
            }
        }
        awaitClose {
            listenerRegistration?.remove()
            channel.close()
        }
    }

    suspend fun observeBloodPressureRecord(): Flow<Result<BloodPressureRecord?>> =
        observe(ObservationCollection.BLOOD_PRESSURE)

    suspend fun observeWeightRecord(): Flow<Result<WeightRecord?>> =
        observe(ObservationCollection.BODY_WEIGHT)

    suspend fun observeHeartRateRecord(): Flow<Result<HeartRateRecord?>> =
        observe(ObservationCollection.HEART_RATE)
}
