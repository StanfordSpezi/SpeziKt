package edu.stanford.bdh.engagehf.health

import androidx.health.connect.client.records.BloodPressureRecord
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.records.Record
import androidx.health.connect.client.records.WeightRecord
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import edu.stanford.bdh.engagehf.health.symptoms.SymptomScore
import edu.stanford.bdh.engagehf.observations.ObservationCollection
import edu.stanford.bdh.engagehf.observations.ObservationCollectionProvider
import edu.stanford.healthconnectonfhir.ObservationsDocumentMapper
import edu.stanford.healthconnectonfhir.RecordToObservationMapper
import edu.stanford.spezi.core.coroutines.di.Dispatching
import edu.stanford.spezi.core.logging.speziLogger
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class HealthRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val observationCollectionProvider: ObservationCollectionProvider,
    private val observationsDocumentMapper: ObservationsDocumentMapper,
    private val observationMapper: ObservationsDocumentMapper,
    private val recordToObservationMapper: RecordToObservationMapper,
    @Dispatching.IO private val ioDispatcher: CoroutineDispatcher,
) {
    private val logger by speziLogger()

    private suspend fun <T : Record> observe(
        collection: ObservationCollection,
        maxMonths: Long = DEFAULT_MAX_MONTHS,
    ): Flow<Result<List<T>>> = observeWithMapper(collection, maxMonths) { document ->
        observationsDocumentMapper.map(document)
    }

    private suspend fun <T> observeWithMapper(
        collection: ObservationCollection,
        maxMonths: Long,
        mapper: (document: com.google.firebase.firestore.DocumentSnapshot) -> T?,
    ): Flow<Result<List<T>>> = callbackFlow {
        var listenerRegistration: ListenerRegistration? = null
        withContext(ioDispatcher) {
            runCatching {
                listenerRegistration =
                    observationCollectionProvider.getCollection(collection).let { query ->
                        if (collection == ObservationCollection.SYMPTOMS) {
                            query.whereGreaterThanOrEqualTo(
                                SYMPTOMS_DATE_FIELD,
                                getDefaultMaxMonthsSymptoms()
                            ).orderBy(
                                SYMPTOMS_DATE_FIELD,
                                Query.Direction.DESCENDING
                            )
                        } else {
                            query.whereGreaterThanOrEqualTo(
                                DATE_TIME_FIELD,
                                getFormattedDate(maxMonths)
                            )
                                .orderBy(DATE_TIME_FIELD, Query.Direction.DESCENDING)
                        }
                    }.addSnapshotListener { snapshot, error ->
                        if (error != null) {
                            logger.e(error) { "Error listening for latest observation in collection: ${collection.name}" }
                            trySend(Result.failure(error))
                        } else {
                            val documents = snapshot?.documents
                            val records = documents?.mapNotNull { document ->
                                mapper(document)
                            } ?: emptyList()
                            trySend(Result.success(records))
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

    suspend fun observeWeightRecords(): Flow<Result<List<WeightRecord>>> =
        observe(ObservationCollection.BODY_WEIGHT)

    suspend fun observeBloodPressureRecords(): Flow<Result<List<BloodPressureRecord>>> =
        observe(ObservationCollection.BLOOD_PRESSURE)

    suspend fun observeHeartRateRecords(): Flow<Result<List<HeartRateRecord>>> =
        observe(ObservationCollection.HEART_RATE)

    suspend fun observeSymptoms(): Flow<Result<List<SymptomScore>>> =
        observeWithMapper(ObservationCollection.SYMPTOMS, DEFAULT_MAX_MONTHS_SYMPTOMS) { document ->
            document.toObject(SymptomScore::class.java)
        }

    private fun getFormattedDate(monthsAgo: Long): String {
        return ZonedDateTime
            .now()
            .minusMonths(monthsAgo)
            .format(DateTimeFormatter.ISO_DATE_TIME)
    }

    private fun getDefaultMaxMonthsSymptoms(): Timestamp {
        val dateAsInstant = ZonedDateTime
            .now()
            .minusMonths(DEFAULT_MAX_MONTHS_SYMPTOMS)
            .toInstant()
        return Timestamp(dateAsInstant)
    }

    suspend fun saveRecord(record: Record): Result<Unit> {
        val observations = recordToObservationMapper.map(record)
        return withContext(ioDispatcher) {
            runCatching {
                val batch = firestore.batch()
                observations.forEach { observation ->
                    val collection = ObservationCollection.entries.find { collection ->
                        observation.code.coding.any { it.code == collection.loinc?.code }
                    }
                    val data = observationMapper.map(observation = observation)
                    collection?.let { observationCollectionProvider.getCollection(it).document() }
                        ?.let { docRef ->
                            batch.set(docRef, data)
                        }
                }
                batch.commit().await().let { }
            }
        }
    }

    private suspend fun deleteRecord(
        recordId: String,
        observationCollection: ObservationCollection,
    ): Result<Unit> {
        return withContext(ioDispatcher) {
            runCatching {
                observationCollectionProvider.getCollection(observationCollection)
                    .document(recordId).delete().await().let { }
            }
        }
    }

    suspend fun deleteWeightRecord(recordId: String): Result<Unit> {
        return deleteRecord(recordId, ObservationCollection.BODY_WEIGHT)
    }

    suspend fun deleteBloodPressureRecord(recordId: String): Result<Unit> {
        return deleteRecord(recordId, ObservationCollection.BLOOD_PRESSURE)
    }

    suspend fun deleteHeartRateRecord(recordId: String): Result<Unit> {
        return deleteRecord(recordId, ObservationCollection.HEART_RATE)
    }

    companion object {
        const val DEFAULT_MAX_MONTHS = 6L
        const val DEFAULT_MAX_MONTHS_SYMPTOMS = 3L
        const val DATE_TIME_FIELD = "effectiveDateTime"
        const val SYMPTOMS_DATE_FIELD = "date"
    }
}
