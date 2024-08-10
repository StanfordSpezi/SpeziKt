package edu.stanford.bdh.engagehf.health

import androidx.health.connect.client.records.BloodPressureRecord
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.records.Record
import androidx.health.connect.client.records.WeightRecord
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import edu.stanford.bdh.engagehf.health.symptoms.SymptomScore
import edu.stanford.bdh.engagehf.observations.ObservationCollection
import edu.stanford.bdh.engagehf.observations.ObservationCollectionProvider
import edu.stanford.healthconnectonfhir.ObservationsDocumentMapper
import edu.stanford.spezi.core.coroutines.di.Dispatching
import edu.stanford.spezi.core.logging.speziLogger
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.withContext
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import javax.inject.Inject

class HealthRepository @Inject constructor(
    private val observationCollectionProvider: ObservationCollectionProvider,
    private val observationsDocumentMapper: ObservationsDocumentMapper,
    @Dispatching.IO private val ioDispatcher: CoroutineDispatcher,
) {
    private val logger by speziLogger()

    suspend fun observeSymptoms(): Flow<Result<List<SymptomScore>>> =
        observeWithMapper(ObservationCollection.SYMPTOMS, DEFAULT_MAX_MONTHS_SYMPTOMS) { document ->
            document.toObject(SymptomScore::class.java)
        }

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

    private fun getFormattedDate(monthsAgo: Long): String {
        return ZonedDateTime
            .now()
            .minusMonths(monthsAgo)
            .format(DateTimeFormatter.ISO_DATE_TIME)
    }

    private fun getDefaultMaxMonthsSymptoms(): Timestamp {
        val date = ZonedDateTime.now().minusMonths(DEFAULT_MAX_MONTHS_SYMPTOMS)
        val dateAsInstant = Date.from(date.toInstant())
        return Timestamp(dateAsInstant)
    }

    companion object {
        const val DEFAULT_MAX_MONTHS = 6L
        const val DEFAULT_MAX_MONTHS_SYMPTOMS = 3L
        const val DATE_TIME_FIELD = "effectiveDateTime"
        const val SYMPTOMS_DATE_FIELD = "date"
    }
}
