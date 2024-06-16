package edu.stanford.healthconnectonfhir

import androidx.health.connect.client.records.ActiveCaloriesBurnedRecord
import androidx.health.connect.client.records.BloodPressureRecord
import androidx.health.connect.client.records.BodyFatRecord
import androidx.health.connect.client.records.BodyTemperatureRecord
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.records.HeightRecord
import androidx.health.connect.client.records.OxygenSaturationRecord
import androidx.health.connect.client.records.Record
import androidx.health.connect.client.records.RespiratoryRateRecord
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.records.WeightRecord
import org.hl7.fhir.r4.model.CodeableConcept
import org.hl7.fhir.r4.model.Coding
import org.hl7.fhir.r4.model.DateTimeType
import org.hl7.fhir.r4.model.Identifier
import org.hl7.fhir.r4.model.Observation
import org.hl7.fhir.r4.model.Period
import org.hl7.fhir.r4.model.Quantity
import java.util.Date
import javax.inject.Inject

class RecordToObservationMapperImpl @Inject constructor() : RecordToObservationMapper {
    /**
     * Maps a given Health Connect record to a list of HL7 FHIR Observations.
     *
     * @param T the type of the health record, extending from `Record`
     * @param record the health record to be mapped
     * @return a list of `Observation` objects derived from the provided health record
     */
    override fun <T : Record> map(record: T): List<Observation> {
        return when (record) {
            is ActiveCaloriesBurnedRecord -> listOf(mapActiveCaloriesBurnedRecord(record))
            is BodyFatRecord -> listOf(mapBodyFatRecord(record))
            is BodyTemperatureRecord -> listOf(mapBodyTemperatureRecord(record))
            is BloodPressureRecord -> listOf(mapBloodPressureRecord(record))
            is HeartRateRecord -> mapHeartRateRecord(record)
            is HeightRecord -> listOf(mapHeightRecord(record))
            is OxygenSaturationRecord -> listOf(mapOxygenSaturationRecord(record))
            is RespiratoryRateRecord -> listOf(mapRespiratoryRateRecord(record))
            is StepsRecord -> listOf(mapStepsRecord(record))
            is WeightRecord -> listOf(mapWeightRecord(record))
            else -> error("Unsupported record type ${record.javaClass.name}")
        }
    }

    private fun mapActiveCaloriesBurnedRecord(record: ActiveCaloriesBurnedRecord) = record.createObservation(
        categories = listOf(
            Coding()
                .setSystem("http://terminology.hl7.org/CodeSystem/observation-category")
                .setCode("activity")
                .setDisplay("Activity")
        ),
        codings = listOf(
            Coding()
                .setSystem("http://loinc.org")
                .setCode("41981-2")
                .setDisplay("Calories burned")
        ),
        unit = MappedUnit(
            code = "kcal",
            unit = "kcal",
            system = "http://unitsofmeasure.org"
        ),
        valueExtractor = { energy.inCalories },
        periodExtractor = { Date.from(startTime) to Date.from(endTime) }
    )
    
    private fun mapBloodPressureRecord(record: BloodPressureRecord): Observation {
        val observation = Observation()

        observation.addCommonElements()

        observation.identifier = listOf(
            Identifier().apply {
                this.setId(record.metadata.id)
            }
        )

        observation.category = listOf(
            CodeableConcept().addCoding(
                Coding()
                    .setSystem("http://terminology.hl7.org/CodeSystem/observation-category")
                    .setCode("vital-signs")
                    .setDisplay("Vital Signs")
            )
        )

        observation.code = CodeableConcept().addCoding(
            Coding()
                .setSystem("http://loinc.org")
                .setCode("85354-9")
                .setDisplay("Blood pressure panel with all children optional")
        )

        val dateTime = DateTimeType(Date.from(record.time))
        observation.effective = dateTime

        val systolicComponent = Observation.ObservationComponentComponent()
        systolicComponent.code = CodeableConcept().addCoding(
            Coding()
                .setSystem("http://loinc.org")
                .setCode("8480-6")
                .setDisplay("Systolic blood pressure")
        )
        systolicComponent.value = Quantity()
            .setValue(record.systolic.inMillimetersOfMercury)
            .setUnit("mmHg")
            .setCode("mm[Hg]")
            .setSystem("http://unitsofmeasure.org")

        val diastolicComponent = Observation.ObservationComponentComponent()
        diastolicComponent.code = CodeableConcept().addCoding(
            Coding()
                .setSystem("http://loinc.org")
                .setCode("8462-4")
                .setDisplay("Diastolic blood pressure")
        )
        diastolicComponent.value = Quantity()
            .setValue(record.diastolic.inMillimetersOfMercury)
            .setUnit("mmHg")
            .setCode("mm[Hg]")
            .setSystem("http://unitsofmeasure.org")

        observation.addComponent(systolicComponent)
        observation.addComponent(diastolicComponent)

        return observation
    }

    private fun mapBodyFatRecord(record: BodyFatRecord) = record.createObservation(
        codings = listOf(
            Coding()
                .setSystem("http://loinc.org")
                .setCode("41982-0")
                .setDisplay("Percentage of body fat Measured")
        ),
        unit = MappedUnit(
            code = "%",
            system = "http://unitsofmeasure.org",
            unit = "%"
        ),
        valueExtractor = { percentage.value },
        periodExtractor = { Date.from(time) to Date.from(time) }
    )

    private fun mapBodyTemperatureRecord(record: BodyTemperatureRecord) = record.createObservation(
        categories = listOf(
            Coding()
                .setSystem("http://terminology.hl7.org/CodeSystem/observation-category")
                .setCode("vital-signs")
                .setDisplay("Vital Signs")
        ),
        codings = listOf(
            Coding()
                .setSystem("http://loinc.org")
                .setCode("8310-5")
                .setDisplay("Body temperature")
        ),
        unit = MappedUnit(
            code = "Cel",
            system = "http://unitsofmeasure.org",
            unit = "C"
        ),
        valueExtractor = { temperature.inCelsius },
        periodExtractor = { Date.from(time) to Date.from(time) }
    )

    private fun mapHeartRateRecord(record: HeartRateRecord): List<Observation> {
        return record.samples.map { sample ->
            val observation = Observation()

            observation.addCommonElements()

            observation.category = listOf(
                CodeableConcept().addCoding(
                    Coding()
                        .setSystem("http://terminology.hl7.org/CodeSystem/observation-category")
                        .setCode("vital-signs")
                        .setDisplay("Vital Signs")
                )
            )

            observation.code = CodeableConcept().addCoding(
                Coding()
                    .setSystem("http://loinc.org")
                    .setCode("8867-4")
                    .setDisplay("Heart rate")
            )

            val dateTime = DateTimeType(Date.from(sample.time))
            observation.effective = dateTime

            observation.value = Quantity()
                .setValue(sample.beatsPerMinute)
                .setUnit("beats/minute")
                .setCode("/min")
                .setSystem("http://unitsofmeasure.org")

            observation
        }
    }

    private fun mapHeightRecord(record: HeightRecord) = record.createObservation(
        categories = listOf(
            Coding()
                .setSystem("http://terminology.hl7.org/CodeSystem/observation-category")
                .setCode("vital-signs")
                .setDisplay("Vital Signs")
        ),
        codings = listOf(
            Coding()
                .setSystem("http://loinc.org")
                .setCode("8302-2")
                .setDisplay("Body height")
        ),
        unit = MappedUnit(
            code = "m",
            system = "http://unitsofmeasure.org",
            unit = "m"
        ),
        valueExtractor = { height.inMeters },
        periodExtractor = { Date.from(time) to Date.from(time) }
    )

    private fun mapOxygenSaturationRecord(record: OxygenSaturationRecord) = record.createObservation(
        categories = listOf(
            Coding()
                .setSystem("http://terminology.hl7.org/CodeSystem/observation-category")
                .setCode("vital-signs")
                .setDisplay("Vital Signs")
        ),
        codings = listOf(
            Coding()
                .setSystem("http://loinc.org")
                .setCode("59408-5")
                .setDisplay("Oxygen saturation in Arterial blood by Pulse oximetry")
        ),
        unit = MappedUnit(
            code = "%",
            system = "http://unitsofmeasure.org",
            unit = "%"
        ),
        valueExtractor = { percentage.value },
        periodExtractor = { Date.from(time) to Date.from(time) }
    )

    private fun mapRespiratoryRateRecord(record: RespiratoryRateRecord) = record.createObservation(
        categories = listOf(
            Coding()
                .setSystem("http://terminology.hl7.org/CodeSystem/observation-category")
                .setCode("vital-signs")
                .setDisplay("Vital Signs")
        ),
        codings = listOf(
            Coding()
                .setSystem("http://loinc.org")
                .setCode("9279-1")
                .setDisplay("Respiratory rate")
        ),
        unit = MappedUnit(
            code = "/min",
            system = "http://unitsofmeasure.org",
            unit = "breaths/minute"
        ),
        valueExtractor = { rate },
        periodExtractor = { Date.from(time) to Date.from(time) }
    )

    private fun mapStepsRecord(record: StepsRecord) = record.createObservation(
        categories = listOf(
            Coding()
                .setSystem("http://terminology.hl7.org/CodeSystem/observation-category")
                .setCode("activity")
                .setDisplay("Activity")
        ),
        codings = listOf(
            Coding()
                .setSystem("http://loinc.org")
                .setCode("55423-8")
                .setDisplay("Number of steps")
        ),
        unit = MappedUnit(
            unit = "steps",
            code = "",
            system = ""
        ),
        valueExtractor = { count.toDouble() },
        periodExtractor = { Date.from(startTime) to Date.from(endTime) }
    )

    private fun mapWeightRecord(record: WeightRecord) = record.createObservation(
        categories = listOf(
            Coding()
                .setSystem("http://terminology.hl7.org/CodeSystem/observation-category")
                .setCode("vital-signs")
                .setDisplay("Vital Signs")
        ),
        codings = listOf(
            Coding()
                .setSystem("http://loinc.org")
                .setCode("29463-7")
                .setDisplay("Body weight")
        ),
        unit = MappedUnit(
            code = "kg",
            system = "http://unitsofmeasure.org",
            unit = "kg"
        ),
        valueExtractor = { weight.inKilograms },
        periodExtractor = { Date.from(time) to Date.from(time) }
    )

    private fun Observation.addCommonElements() {
        this.setStatus(Observation.ObservationStatus.FINAL)
        this.setIssued(Date())
    }

    private fun <T : Record> T.createObservation(
        categories: List<Coding> = listOf(),
        codings: List<Coding>,
        unit: MappedUnit,
        valueExtractor: T.() -> Double,
        periodExtractor: T.() -> Pair<Date, Date>,
    ): Observation {
        return Observation().apply {
            addCommonElements()

            identifier = listOf(Identifier().apply {
                this.setId(this@createObservation.metadata.id)
            })

            category = listOf(CodeableConcept().apply {
                categories.forEach { addCoding(it) }
            })

            code = CodeableConcept().apply {
                codings.forEach { addCoding(it) }
            }

            val (start, end) = periodExtractor()

            if (start == end) {
                effective = DateTimeType().apply {
                    this.value = start
                }
            } else {
                effective = Period().apply {
                    this.start = start
                    this.end = end
                }
            }

            value = Quantity().apply {
                this.value = valueExtractor().toBigDecimal()
                this.unit = unit.unit
                this.code = unit.code
                this.system = unit.system
            }
        }
    }
}
