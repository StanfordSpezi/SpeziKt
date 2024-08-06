package edu.stanford.bdh.engagehf.health

import androidx.health.connect.client.records.BloodPressureRecord
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.records.Record
import androidx.health.connect.client.records.WeightRecord
import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.context.FhirVersionEnum
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.gson.Gson
import edu.stanford.healthconnectonfhir.ObservationToRecordMapper
import edu.stanford.spezi.core.coroutines.di.Dispatching
import edu.stanford.spezi.core.logging.speziLogger
import edu.stanford.spezi.module.account.manager.UserSessionManager
import edu.stanford.spezi.modules.measurements.ObservationCollection
import edu.stanford.spezi.modules.measurements.bloodPressureCollection
import edu.stanford.spezi.modules.measurements.bodyWeightObservation
import edu.stanford.spezi.modules.measurements.heartRateCollection
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.withContext
import org.hl7.fhir.r4.model.Observation
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class HealthRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val userSessionManager: UserSessionManager,
    private val observationToRecordMapper: ObservationToRecordMapper,
    @Dispatching.IO private val ioDispatcher: CoroutineDispatcher,
) {
    private val logger by speziLogger()

    private val jsonParser by lazy {
        FhirContext.forCached(FhirVersionEnum.R4).newJsonParser()
    }

    private val gson by lazy {
        Gson()
    }

    private suspend fun <T : Record> observe(
        collection: ObservationCollection,
    ): Flow<Result<List<T>>> =
        callbackFlow {
            var listenerRegistration: ListenerRegistration? = null
            withContext(ioDispatcher) {
                kotlin.runCatching {
                    val uid = userSessionManager.getUserUid()
                        ?: throw IllegalStateException("User not authenticated")
                    val fromDateString = ZonedDateTime
                        .now()
                        .minusMonths(DEFAULT_MAX_MONTHS)
                        .format(DateTimeFormatter.ISO_DATE_TIME)
                    listenerRegistration = firestore.collection("users/$uid/${collection.name}")
                        .whereGreaterThanOrEqualTo(DATE_TIME_FIELD, fromDateString)
                        .orderBy(DATE_TIME_FIELD, Query.Direction.DESCENDING)
                        .addSnapshotListener { snapshot, error ->
                            if (error != null) {
                                logger.e(error) { "Error listening for latest observation in collection: ${collection.name}" }
                                trySend(Result.failure(error))
                            } else {
                                val documents = snapshot?.documents
                                val records: List<T> = documents?.mapNotNull { document ->
                                    val json = gson.toJson(document.data)
                                    val observation = jsonParser.parseResource(Observation::class.java, json)
                                    observationToRecordMapper.map(observation)
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
        observe(bodyWeightObservation)

    suspend fun observeBloodPressureRecords(): Flow<Result<List<BloodPressureRecord>>> =
        observe(bloodPressureCollection)

    suspend fun observeHeartRateRecords(): Flow<Result<List<HeartRateRecord>>> =
        observe(heartRateCollection)

    companion object {
        const val DEFAULT_MAX_MONTHS = 6L
        const val DATE_TIME_FIELD = "effectiveDateTime"
    }
}
