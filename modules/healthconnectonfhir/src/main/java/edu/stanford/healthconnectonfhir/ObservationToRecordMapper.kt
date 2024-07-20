package edu.stanford.healthconnectonfhir

import androidx.health.connect.client.records.Record
import org.hl7.fhir.r4.model.Observation

interface ObservationToRecordMapper {
    fun <T : Record> map(observation: Observation): T
}
