package edu.stanford.bdh.engagehf.health

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
import edu.stanford.spezi.modules.measurements.bodyWeightObservation
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.withContext
import org.hl7.fhir.r4.model.Observation
import java.time.ZonedDateTime
import javax.inject.Inject

class HealthRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val userSessionManager: UserSessionManager,
    private val observationToRecordMapper: ObservationToRecordMapper,
    @Dispatching.IO private val ioDispatcher: CoroutineDispatcher,
) {
    @Suppress("MagicNumber")

    private val logger by speziLogger()

    private val jsonParser by lazy {
        FhirContext.forCached(FhirVersionEnum.R4).newJsonParser()
    }

    private val gson by lazy {
        Gson()
    }

    private suspend fun <T : Record> observe(
        collection: ObservationCollection,
        startDateTime: ZonedDateTime,
        endDateTime: ZonedDateTime,
    ): Flow<Result<List<T>>> =
        callbackFlow {
            var listenerRegistration: ListenerRegistration? = null
            withContext(ioDispatcher) {
                kotlin.runCatching {
                    val uid = userSessionManager.getUserUid()
                        ?: throw IllegalStateException("User not authenticated")
                    listenerRegistration = firestore.collection("users/$uid/${collection.name}")
                        .whereGreaterThanOrEqualTo("effectiveDateTime", startDateTime)
                        .whereLessThanOrEqualTo("effectiveDateTime", endDateTime)
                        .orderBy("effectiveDateTime", Query.Direction.DESCENDING)
                        .addSnapshotListener { snapshot, error ->
                            if (error != null) {
                                logger.e(error) { "Error listening for latest observation in collection: ${collection.name}" }
                                trySend(Result.failure(error))
                            } else {
                                val documents = snapshot?.documents
                                val records = documents?.mapNotNull { document ->
                                    val json = gson.toJson(document.data)
                                    val observation =
                                        jsonParser.parseResource(Observation::class.java, json)
                                    val record = observationToRecordMapper.map(observation) as T
                                    record
                                }
                                records?.let {
                                    trySend(Result.success(records))
                                }
                                trySend(Result.success(emptyList()))
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

    suspend fun observeWeightRecords(
        startDateTime: ZonedDateTime,
        endDateTime: ZonedDateTime,
    ): Flow<Result<List<WeightRecord>>> =
        observe(bodyWeightObservation, startDateTime, endDateTime)

    private var weights = listOf(
        HealthRecord(
            value = 80.0f,
            zonedDateTime = ZonedDateTime.now().minusDays(1),
            trend = 1.0f
        ),
        HealthRecord(
            value = 81.0f,
            zonedDateTime = ZonedDateTime.now().minusDays(5),
            trend = -1.0f
        ),
        HealthRecord(
            value = 82.0f,
            zonedDateTime = ZonedDateTime.now().minusDays(7),
            trend = 2.0f
        ),
        HealthRecord(
            value = 83.0f,
            zonedDateTime = ZonedDateTime.now().minusDays(14),
            trend = -2.0f
        ),

        HealthRecord(
            value = 84.0f,
            zonedDateTime = ZonedDateTime.now().minusDays(30),
            trend = 3.0f
        ),

        HealthRecord(
            value = 85.0f,
            zonedDateTime = ZonedDateTime.now().minusDays(60),
            trend = -3.0f
        ),
        HealthRecord(
            value = 86.0f,
            zonedDateTime = ZonedDateTime.now().minusDays(90),
            trend = 4.0f
        ),
        HealthRecord(
            value = 87.0f,
            zonedDateTime = ZonedDateTime.now().minusDays(180),
            trend = -4.0f
        ),
    ).sortedBy { it.zonedDateTime }

    fun getWeights(): List<HealthRecord> {
        return weights
    }

    fun deleteWeight(weightId: String) {
        weights = weights.filter { it.id != weightId }
    }
}
