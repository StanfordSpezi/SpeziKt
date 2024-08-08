package edu.stanford.bdh.engagehf.bluetooth.measurements

import androidx.health.connect.client.records.BloodPressureRecord
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.records.Record
import androidx.health.connect.client.records.WeightRecord
import androidx.health.connect.client.units.Mass
import androidx.health.connect.client.units.Pressure
import edu.stanford.healthconnectonfhir.RecordToObservationMapper
import edu.stanford.spezi.core.bluetooth.data.model.Measurement
import org.hl7.fhir.r4.model.Observation
import java.time.LocalDateTime
import java.time.ZonedDateTime
import javax.inject.Inject

internal class MeasurementToObservationMapper @Inject constructor(
    private val recordToObservationMapper: RecordToObservationMapper,
) {

    private val currentZoneOffset get() = ZonedDateTime.now().offset

    fun map(measurement: Measurement): List<Observation> {
        return mapToRecords(measurement)
            .flatMap { recordToObservationMapper.map(it) }
    }

    private fun mapToRecords(measurement: Measurement): List<Record> {
        return when (measurement) {
            is Measurement.BloodPressure -> {
                listOf(createHeartRateRecord(measurement), createBloodPressureRecord(measurement))
            }

            is Measurement.Weight -> {
                listOf(createWeightRecord(measurement))
            }
        }
    }

    private fun createBloodPressureRecord(measurement: Measurement.BloodPressure): BloodPressureRecord {
        return BloodPressureRecord(
            systolic = Pressure.millimetersOfMercury(measurement.systolic.toDouble()),
            diastolic = Pressure.millimetersOfMercury(measurement.diastolic.toDouble()),
            time = createLocalDateTime(measurement).toInstant(
                currentZoneOffset
            ),
            zoneOffset = currentZoneOffset
        )
    }

    private fun createHeartRateRecord(measurement: Measurement.BloodPressure): HeartRateRecord {
        return HeartRateRecord(
            startTime = createLocalDateTime(measurement).toInstant(
                currentZoneOffset
            ),
            endTime = createLocalDateTime(measurement).toInstant(
                currentZoneOffset
            ),
            startZoneOffset = currentZoneOffset,
            endZoneOffset = currentZoneOffset,
            samples = listOf(
                HeartRateRecord.Sample(
                    time = createLocalDateTime(measurement).toInstant(
                        currentZoneOffset
                    ),
                    beatsPerMinute = measurement.pulseRate.toLong()
                )
            )
        )
    }

    private fun createWeightRecord(measurement: Measurement.Weight): WeightRecord {
        return WeightRecord(
            weight = Mass.kilograms(measurement.weight),
            time = measurement.zonedDateTime?.toInstant() ?: ZonedDateTime.now().toInstant(),
            zoneOffset = currentZoneOffset
        )
    }

    private fun createLocalDateTime(measurement: Measurement.BloodPressure): LocalDateTime {
        return LocalDateTime.of(
            measurement.timestampYear,
            measurement.timestampMonth,
            measurement.timestampDay,
            measurement.timeStampHour,
            measurement.timeStampMinute,
            measurement.timeStampSecond
        )
    }
}
