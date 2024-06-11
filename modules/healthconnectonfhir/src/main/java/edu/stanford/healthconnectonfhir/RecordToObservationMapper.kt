package edu.stanford.healthconnectonfhir

import androidx.health.connect.client.records.Record
import org.hl7.fhir.r4.model.Observation

interface RecordToObservationMapper {
    /**
     * Maps a given Health Connect record to a list of HL7 FHIR Observations
     *
     * @param T the type of the Health Connect record, extending from `Record`
     * @param record the record to be mapped
     * @return a list of `Observation` objects derived from the provided health record
     */
    fun <T: Record> map(record: T): List<Observation>
}