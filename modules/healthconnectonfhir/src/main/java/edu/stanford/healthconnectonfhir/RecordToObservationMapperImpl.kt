package edu.stanford.healthconnectonfhir

import androidx.health.connect.client.records.ActiveCaloriesBurnedRecord
import androidx.health.connect.client.records.BloodGlucoseRecord
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
     * @param T the type of the health record, extending from Record
     * @param record the health record to be mapped
     * @return a list of Observation objects derived from the provided health record
     * @throws IllegalArgumentException if the record type is unsupported
     */
    override fun <T : Record> map(record: T): List<Observation> {
        return when (record) {
            is ActiveCaloriesBurnedRecord -> listOf(mapActiveCaloriesBurnedRecord(record))
            is BodyFatRecord -> listOf(mapBodyFatRecord(record))
            is BodyTemperatureRecord -> listOf(mapBodyTemperatureRecord(record))
            is BloodGlucoseRecord -> listOf(mapBloodGlucoseRecord(record))
            is BloodPressureRecord -> listOf(mapBloodPressureRecord(record))
            is HeartRateRecord -> mapHeartRateRecord(record)
            is HeightRecord -> listOf(mapHeightRecord(record))
            is OxygenSaturationRecord -> listOf(mapOxygenSaturationRecord(record))
            is RespiratoryRateRecord -> listOf(mapRespiratoryRateRecord(record))
            is StepsRecord -> listOf(mapStepsRecord(record))
            is WeightRecord -> listOf(mapWeightRecord(record))
            else -> throw IllegalArgumentException("Unsupported record type ${record.javaClass.name}")
        }
    }

    /**
     * Maps an ActiveCaloriesBurnedRecord to a FHIR Observation.
     *
     * @param record the ActiveCaloriesBurnedRecord to be mapped
     * @return an Observation object derived from the provided ActiveCaloriesBurnedRecord
     */
    private fun mapActiveCaloriesBurnedRecord(record: ActiveCaloriesBurnedRecord) = record.createObservation(
        categories = listOf(
            Coding()
                .setSystem("http://terminology.hl7.org/CodeSystem/observation-category")
                .setCode("activity")
                .setDisplay("Activity"),
        ),
        codings = listOf(
            Coding()
                .setSystem("http://loinc.org")
                .setCode("41981-2")
                .setDisplay("Calories burned"),
            Coding()
                .setSystem("http://health.google/health-connect-android/")
                .setCode("ActiveCaloriesBurnedRecord")
                .setDisplay("Active Calories Burned Record"),
        ),
        unit = MappedUnit(
            code = "kcal",
            unit = "kcal",
            system = "http://unitsofmeasure.org"
        ),
        valueExtractor = { energy.inCalories },
        periodExtractor = { Date.from(startTime) to Date.from(endTime) }
    )

    /**
     * Maps a BloodGlucoseRecord to a FHIR Observation.
     *
     * @param record the BloodGlucoseRecord to be mapped
     * @return an Observation object derived from the provided BloodGlucoseRecord
     */
    private fun mapBloodGlucoseRecord(record: BloodGlucoseRecord) = record.createObservation(
        codings = listOf(
            Coding()
                .setSystem("http://loinc.org")
                .setCode("41653-7")
                .setDisplay("Glucose Glucometer (BldC) [Mass/Vol]"),
            Coding()
                .setSystem("http://health.google/health-connect-android/")
                .setCode("BloodGlucoseRecord")
                .setDisplay("Blood Glucose Record"),
        ),
        unit = MappedUnit(
            code = "mg/dL",
            unit = "mg/dL",
            system = "http://unitsofmeasure.org"
        ),
        valueExtractor = { level.inMilligramsPerDeciliter },
        periodExtractor = { Date.from(time) to Date.from(time) }
    )

    /**
     * Maps a BloodPressureRecord to a FHIR Observation.
     *
     * @param record the BloodPressureRecord to be mapped
     * @return an Observation object derived from the provided BloodPressureRecord in which
     * the systolic and diastolic blood pressure values are represented as separate components.
     */
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

        val codings = listOf(
            Coding()
                .setSystem("http://loinc.org")
                .setCode("85354-9")
                .setDisplay("Blood pressure panel with all children optional"),
            Coding()
                .setSystem("http://health.google/health-connect-android/")
                .setCode("BloodPressureRecord")
                .setDisplay("Blood Pressure Record"),
        )

        observation.code = CodeableConcept().apply {
            codings.forEach { addCoding(it) }
        }

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

    /**
     * Maps a BodyFatRecord to a FHIR Observation.
     *
     * @param record the BodyFatRecord to be mapped
     * @return an Observation object derived from the provided BodyFatRecord
     */
    private fun mapBodyFatRecord(record: BodyFatRecord) = record.createObservation(
        codings = listOf(
            Coding()
                .setSystem("http://loinc.org")
                .setCode("41982-0")
                .setDisplay("Percentage of body fat Measured"),
            Coding()
                .setSystem("http://health.google/health-connect-android/")
                .setCode("BodyFatRecord")
                .setDisplay("Body Fat Record"),
        ),
        unit = MappedUnit(
            code = "%",
            system = "http://unitsofmeasure.org",
            unit = "%"
        ),
        valueExtractor = { percentage.value },
        periodExtractor = { Date.from(time) to Date.from(time) }
    )

    /**
     * Maps a BodyTemperatureRecord to a FHIR Observation.
     *
     * @param record the BodyTemperatureRecord to be mapped
     * @return an Observation object derived from the provided BodyTemperatureRecord
     */
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
                .setDisplay("Body temperature"),
            Coding()
                .setSystem("http://health.google/health-connect-android/")
                .setCode("BodyTemperatureRecord")
                .setDisplay("Body Temperature Record"),
        ),
        unit = MappedUnit(
            code = "Cel",
            system = "http://unitsofmeasure.org",
            unit = "C"
        ),
        valueExtractor = { temperature.inCelsius },
        periodExtractor = { Date.from(time) to Date.from(time) }
    )

    /**
     * Maps a HeartRateRecord to a list of FHIR Observations.
     *
     * @param record the HeartRateRecord to be mapped
     * @return a list of Observation objects derived from the provided HeartRateRecord.
     * Each object represents a single sample from the HeartRateRecord.
     */
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

            val codings = listOf(
                Coding()
                    .setSystem("http://loinc.org")
                    .setCode("8867-4")
                    .setDisplay("Heart rate"),
                Coding()
                    .setSystem("http://health.google/health-connect-android/")
                    .setCode("HeartRateRecord")
                    .setDisplay("Heart Rate Record")
            )

            observation.code = CodeableConcept().apply {
                codings.forEach { addCoding(it) }
            }

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

    /**
     * Maps a HeightRecord to a FHIR Observation.
     *
     * @param record the HeightRecord to be mapped
     * @return an Observation object derived from the provided HeightRecord
     */
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
                .setDisplay("Body height"),
            Coding()
                .setSystem("http://health.google/health-connect-android/")
                .setCode("HeightRecord")
                .setDisplay("Height Record"),
        ),
        unit = MappedUnit(
            code = "m",
            system = "http://unitsofmeasure.org",
            unit = "m"
        ),
        valueExtractor = { height.inMeters },
        periodExtractor = { Date.from(time) to Date.from(time) }
    )

    /**
     * Maps an OxygenSaturationRecord to a FHIR Observation.
     *
     * @param record the OxygenSaturationRecord to be mapped
     * @return an Observation object derived from the provided OxygenSaturationRecord
     */
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
                .setDisplay("Oxygen saturation in Arterial blood by Pulse oximetry"),
            Coding()
                .setSystem("http://health.google/health-connect-android/")
                .setCode("OxygenSaturationRecord")
                .setDisplay("Oxygen Saturation Record"),
        ),
        unit = MappedUnit(
            code = "%",
            system = "http://unitsofmeasure.org",
            unit = "%"
        ),
        valueExtractor = { percentage.value },
        periodExtractor = { Date.from(time) to Date.from(time) }
    )

    /**
     * Maps a RespiratoryRateRecord to a FHIR Observation.
     *
     * @param record the RespiratoryRateRecord to be mapped
     * @return an Observation object derived from the provided RespiratoryRateRecord
     */
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
                .setDisplay("Respiratory rate"),
            Coding()
                .setSystem("http://health.google/health-connect-android/")
                .setCode("RespiratoryRateRecord")
                .setDisplay("Respiratory Rate Record"),
        ),
        unit = MappedUnit(
            code = "/min",
            system = "http://unitsofmeasure.org",
            unit = "breaths/minute"
        ),
        valueExtractor = { rate },
        periodExtractor = { Date.from(time) to Date.from(time) }
    )

    /**
     * Maps a StepsRecord to a FHIR Observation.
     *
     * @param record the StepsRecord to be mapped
     * @return an Observation object derived from the provided StepsRecord
     */
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
                .setDisplay("Number of steps"),
            Coding()
                .setSystem("http://health.google/health-connect-android/")
                .setCode("StepsRecord")
                .setDisplay("Steps Record"),
        ),
        unit = MappedUnit(
            unit = "steps",
            code = "",
            system = ""
        ),
        valueExtractor = { count.toDouble() },
        periodExtractor = { Date.from(startTime) to Date.from(endTime) }
    )

    /**
     * Maps a WeightRecord to a FHIR Observation.
     *
     * @param record the WeightRecord to be mapped
     * @return an Observation object derived from the provided WeightRecord
     */
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
                .setDisplay("Body weight"),
            Coding()
                .setSystem("http://health.google/health-connect-android/")
                .setCode("WeightRecord")
                .setDisplay("Weight Record"),
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
