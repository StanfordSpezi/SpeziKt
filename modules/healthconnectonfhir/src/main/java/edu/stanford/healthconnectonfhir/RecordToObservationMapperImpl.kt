package edu.stanford.healthconnectonfhir

import androidx.health.connect.client.records.ActiveCaloriesBurnedRecord
import androidx.health.connect.client.records.BloodPressureRecord
import androidx.health.connect.client.records.BodyTemperatureRecord
import androidx.health.connect.client.records.HeightRecord
import androidx.health.connect.client.records.Record
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.records.WeightRecord
import org.hl7.fhir.r4.model.CodeableConcept
import org.hl7.fhir.r4.model.Coding
import org.hl7.fhir.r4.model.Observation
import org.hl7.fhir.r4.model.Period
import org.hl7.fhir.r4.model.Quantity
import java.util.Date

class RecordToObservationMapperImpl : RecordToObservationMapper {
    override fun <T: Record> map(record: T): Observation {
        return when (record) {
            is StepsRecord -> mapStepsRecord(record)
            is WeightRecord -> mapWeightRecord(record)
            is HeightRecord -> mapHeightRecord(record)
            is BodyTemperatureRecord -> mapBodyTemperatureRecord(record)
            is BloodPressureRecord -> mapBloodPressureRecord(record)
            is ActiveCaloriesBurnedRecord -> mapActiveCaloriesBurnedRecord(record)
            else -> error("Unsupported record type ${record.javaClass.name}")
        }
    }

    private fun mapStepsRecord(record: StepsRecord) = record.createObservation(
        categories = listOf(
            Coding().setSystem("http://terminology.hl7.org/CodeSystem/observation-category").setCode("activity").setDisplay("Activity")
        ),
        codings = listOf(
            Coding().setSystem("http://loinc.org").setCode("55423-8").setDisplay("Number of steps")
        ),
        unit = "steps",
        valueExtractor = { count.toDouble() },
        periodExtractor = { Date.from(startTime) to Date.from(endTime) }
    )

    private fun mapWeightRecord(record: WeightRecord) = record.createObservation(
            categories = listOf(
                Coding().setSystem("http://terminology.hl7.org/CodeSystem/observation-category").setCode("vital-signs").setDisplay("Vital Signs")
            ),
            codings = listOf(
                Coding().setSystem("http://loinc.org").setCode("29463-7").setDisplay("Body weight")
            ),
            unit = "g",
            valueExtractor = { weight.inGrams },
            periodExtractor = { Date.from(time) to Date.from(time) }
        )

    private fun mapHeightRecord(record: HeightRecord) = record.createObservation(
            categories = listOf(
                Coding().setSystem("http://terminology.hl7.org/CodeSystem/observation-category").setCode("vital-signs").setDisplay("Vital Signs")
            ),
            codings = listOf(
                Coding().setSystem("http://loinc.org").setCode("8302-2").setDisplay("Body height")
            ),
            unit = "m",
            valueExtractor = { height.inMeters },
            periodExtractor = { Date.from(time) to Date.from(time) }
        )

    private fun mapActiveCaloriesBurnedRecord(record: ActiveCaloriesBurnedRecord) = record.createObservation(
            categories = listOf(
                Coding().setSystem("http://terminology.hl7.org/CodeSystem/observation-category").setCode("activity").setDisplay("Activity")
            ),
            codings = listOf(
                Coding().setSystem("http://loinc.org").setCode("41981-2").setDisplay("Calories burned")
            ),
            unit = "kcal",
            valueExtractor = { energy.inCalories },
            periodExtractor = { Date.from(startTime) to Date.from(endTime) }
        )

    private fun mapBodyTemperatureRecord(record: BodyTemperatureRecord) = record.createObservation(
            categories = listOf(
                Coding().setSystem("http://terminology.hl7.org/CodeSystem/observation-category").setCode("vital-signs").setDisplay("Vital Signs")
            ),
            codings = listOf(
                Coding().setSystem("http://loinc.org").setCode("8310-5").setDisplay("Body temperature")
            ),
            unit = "°C",
            valueExtractor = { temperature.inCelsius },
            periodExtractor = { Date.from(time) to Date.from(time) }
        )

    private fun mapBloodPressureRecord(record: BloodPressureRecord): Observation {
        val observation = Observation()
        observation.status = Observation.ObservationStatus.FINAL

        observation.category = listOf(
            CodeableConcept().addCoding(
                Coding().setSystem("http://terminology.hl7.org/CodeSystem/observation-category").setCode("vital-signs").setDisplay("Vital Signs")
            )
        )

        observation.code = CodeableConcept().addCoding(
            Coding()
                .setSystem("http://loinc.org")
                .setCode("85354-9")
                .setDisplay("Blood pressure panel with all children optional")
        )

        val period = Period()
        period.start = Date.from(record.time)
        period.end = Date.from(record.time)
        observation.effective = period

        val systolicComponent = Observation.ObservationComponentComponent()
        systolicComponent.code = CodeableConcept().addCoding(
            Coding()
                .setSystem("http://loinc.org")
                .setCode("8480-6")
                .setDisplay("Systolic blood pressure")
        )
        systolicComponent.value = Quantity().setValue(record.systolic.inMillimetersOfMercury).setUnit("mmHg")

        val diastolicComponent = Observation.ObservationComponentComponent()
        diastolicComponent.code = CodeableConcept().addCoding(
            Coding()
                .setSystem("http://loinc.org")
                .setCode("8462-4")
                .setDisplay("Diastolic blood pressure")
        )
        diastolicComponent.value = Quantity().setValue(record.diastolic.inMillimetersOfMercury).setUnit("mmHg")

        observation.addComponent(systolicComponent)
        observation.addComponent(diastolicComponent)

        return observation
    }


    private fun <T: Record> T.createObservation(
        categories: List<Coding> = listOf(),
        codings: List<Coding>,
        unit: String,
        valueExtractor: T.() -> Double,
        periodExtractor: T.() -> Pair<Date, Date>
    ): Observation {
        return Observation().apply {
            status = Observation.ObservationStatus.FINAL

            category = listOf(CodeableConcept().apply{
                categories.forEach { addCoding(it) }
            })

            code = CodeableConcept().apply {
                codings.forEach { addCoding(it) }
            }

            effective = Period().apply {
                val (start, end) = periodExtractor()
                this.start = start
                this.end = end
            }

            value = Quantity().apply {
                this.value = valueExtractor().toBigDecimal()
                this.unit = unit
            }
        }
    }
}