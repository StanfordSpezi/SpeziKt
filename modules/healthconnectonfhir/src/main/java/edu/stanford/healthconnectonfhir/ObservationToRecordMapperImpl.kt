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

    private val codeToMappingFunction: Map<String, (Observation) -> Record> = mapOf(
        Loinc.BloodPressure.CODE to { obs -> mapToBloodPressureRecord(obs) },
        Loinc.Weight.CODE to { obs -> mapToWeightRecord(obs) },
        Loinc.HeartRate.CODE to { obs -> mapToHeartRateRecord(obs) }
    )

    override fun map(observation: Observation): Record {
        val code = observation.code.codingFirstRep.code
        val mappingFunction = codeToMappingFunction[code]
            ?: throw IllegalArgumentException("Unsupported observation code $code")
        return mappingFunction(observation)
    }

    private fun mapToHeartRateRecord(observation: Observation): Record {
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
            )
        )
    }

    private fun mapToBloodPressureRecord(observation: Observation): BloodPressureRecord {
        val time = observation.effectiveDateTimeType.value.toInstant()
        val zoneOffset =
            ZoneOffset.ofTotalSeconds(observation.effectiveDateTimeType.value.timezoneOffset)


        val systolic =
            observation.component.first { it.code.codingFirstRep.code == Loinc.BloodPressure.COMPONENT.SYSTOLIC }
                .valueQuantity.value.toDouble()
        val diastolic =
            observation.component.first { it.code.codingFirstRep.code == Loinc.BloodPressure.COMPONENT.DIASTOLIC }
                .valueQuantity.value.toDouble()

        return BloodPressureRecord(
            time = time,
            zoneOffset = zoneOffset,
            systolic = Pressure.millimetersOfMercury(systolic),
            diastolic = Pressure.millimetersOfMercury(diastolic),
        )
    }

    private fun mapToWeightRecord(observation: Observation): WeightRecord {
        val time = observation.effectiveDateTimeType.value.toInstant()
        val zoneOffset =
            ZoneOffset.ofTotalSeconds(observation.effectiveDateTimeType.value.timezoneOffset)

        val weight = observation.valueQuantity.value.toDouble()
        val unit =
            observation.valueQuantity.unit // TODO right now we always store in kg; but gotta implement this properly

        return WeightRecord(
            time = time,
            zoneOffset = zoneOffset,
            weight = Mass.kilograms(weight),
        )
    }
}
