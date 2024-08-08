package edu.stanford.healthconnectonfhir

import androidx.health.connect.client.records.Record
import com.google.firebase.firestore.DocumentSnapshot
import org.hl7.fhir.r4.model.Observation

interface ObservationsDocumentMapper {
    fun <T : Record> map(observationDocument: DocumentSnapshot): T

    fun map(observation: Observation): Map<String, Any>
}
