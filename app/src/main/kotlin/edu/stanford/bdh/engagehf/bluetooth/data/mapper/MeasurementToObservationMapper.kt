package edu.stanford.bdh.engagehf.bluetooth.data.mapper

import edu.stanford.healthconnectonfhir.RecordToObservationMapper
import edu.stanford.spezi.core.bluetooth.data.model.Measurement
import org.hl7.fhir.r4.model.Observation
import javax.inject.Inject

interface MeasurementToObservationMapper {
    fun map(measurement: Measurement): List<Observation>
}

internal class DefaultMeasurementToObservationMapper @Inject constructor(
    private val measurementToRecordMapper: MeasurementToRecordMapper,
    private val recordToObservationMapper: RecordToObservationMapper,
) : MeasurementToObservationMapper {
    override fun map(measurement: Measurement): List<Observation> {
        return measurementToRecordMapper.map(measurement)
            .flatMap { recordToObservationMapper.map(it) }
    }
}
