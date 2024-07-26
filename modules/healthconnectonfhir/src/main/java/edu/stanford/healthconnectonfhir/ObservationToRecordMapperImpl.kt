package edu.stanford.healthconnectonfhir

import androidx.health.connect.client.records.BloodPressureRecord
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.records.Record
import androidx.health.connect.client.records.WeightRecord
import androidx.health.connect.client.units.Mass
import androidx.health.connect.client.units.Pressure
import org.hl7.fhir.r4.model.Observation
import java.time.ZoneOffset
import javax.inject.Inject

class ObservationToRecordMapperImpl @Inject constructor() : ObservationToRecordMapper {

    override fun <T : Record> map(observation: Observation, clientRecordId: String?): T {
        @Suppress("UNCHECKED_CAST")
        return when (observation.code.codingFirstRep.code) {
            Loinc.BloodPressure.CODE -> mapToBloodPressureRecord(observation, clientRecordId)
            Loinc.Weight.CODE -> mapToWeightRecord(observation, clientRecordId)
            Loinc.HeartRate.CODE -> mapToHeartRateRecord(observation, clientRecordId)
            else -> error("Unsupported observation type: ${observation.code.codingFirstRep.code}")
        } as T
    }

    private fun mapToHeartRateRecord(observation: Observation, clientRecordId: String?): Record {
        val metadata = androidx.health.connect.client.records.metadata.Metadata(
            clientRecordId = clientRecordId
        )
        val time = observation.effectiveDateTimeType.value.toInstant()
        val heartRate = observation.valueQuantity.value.toDouble()
        val zoneOffset =
            ZoneOffset.ofTotalSeconds(observation.effectiveDateTimeType.value.timezoneOffset)


        return HeartRateRecord(
            startTime = time,
            startZoneOffset = zoneOffset,
            endTime = time,
            endZoneOffset = zoneOffset,
            samples = listOf(
                HeartRateRecord.Sample(
                    time = time,
                    beatsPerMinute = heartRate.toLong()
                )
            ),
            metadata = metadata
        )
    }

    private fun mapToBloodPressureRecord(
        observation: Observation,
        clientRecordId: String?,
    ): BloodPressureRecord {
        val time = observation.effectiveDateTimeType.value.toInstant()
        val zoneOffset =
            ZoneOffset.ofTotalSeconds(observation.effectiveDateTimeType.value.timezoneOffset)

        val systolic =
            observation.component.first { it.code.codingFirstRep.code == Loinc.BloodPressure.COMPONENT.SYSTOLIC }
                .valueQuantity.value.toDouble()
        val diastolic =
            observation.component.first { it.code.codingFirstRep.code == Loinc.BloodPressure.COMPONENT.DIASTOLIC }
                .valueQuantity.value.toDouble()

        val metadata = androidx.health.connect.client.records.metadata.Metadata(
            clientRecordId = clientRecordId
        )

        return BloodPressureRecord(
            time = time,
            zoneOffset = zoneOffset,
            systolic = Pressure.millimetersOfMercury(systolic),
            diastolic = Pressure.millimetersOfMercury(diastolic),
            metadata = metadata
        )
    }

    private fun mapToWeightRecord(observation: Observation, clientRecordId: String?): WeightRecord {
        val time = observation.effectiveDateTimeType.value.toInstant()
        val zoneOffset =
            ZoneOffset.ofTotalSeconds(observation.effectiveDateTimeType.value.timezoneOffset)

        val weight = observation.valueQuantity.value.toDouble()
        val unit =
            observation.valueQuantity.unit // TODO right now we always store in kg; but gotta implement this properly

        val metadata = androidx.health.connect.client.records.metadata.Metadata(
            clientRecordId = clientRecordId
        )

        return WeightRecord(
            time = time,
            zoneOffset = zoneOffset,
            weight = Mass.kilograms(weight),
            metadata = metadata
        )
    }
}
