package edu.stanford.healthconnectonfhir

import androidx.health.connect.client.records.ActiveCaloriesBurnedRecord
import androidx.health.connect.client.records.BloodPressureRecord
import androidx.health.connect.client.records.BodyTemperatureRecord
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.records.HeightRecord
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.records.WeightRecord
import org.hl7.fhir.r4.model.CodeableConcept
import org.hl7.fhir.r4.model.Coding
import org.hl7.fhir.r4.model.Observation
import org.hl7.fhir.r4.model.Period
import org.hl7.fhir.r4.model.Quantity
import java.util.Date

fun <T> T.toObservation(
    code: String,
    display: String,
    unit: String,
    valueExtractor: T.() -> Double,
    periodExtractor: T.() -> Pair<Date, Date>
): Observation {
    val observation = Observation()
    observation.status = Observation.ObservationStatus.FINAL
    observation.code = CodeableConcept().addCoding(
        Coding()
            .setSystem("http://loinc.org")
            .setCode(code)
            .setDisplay(display)
    )

    val period = Period()
    val (start, end) = this.periodExtractor()
    period.start = start
    period.end = end
    observation.effective = period

    observation.value = Quantity().setValue(this.valueExtractor()).setUnit(unit)

    return observation
}

inline fun StepsRecord.toObservation(): Observation {
    return this.toObservation(
        code = "55423-8",
        display = "Number of steps",
        unit = "steps",
        valueExtractor = { count.toDouble() },
        periodExtractor = { Date.from(startTime) to Date.from(endTime) }
    )
}

inline fun WeightRecord.toObservation(): Observation {
    return this.toObservation(
        code = "29463-7",
        display = "Body weight",
        unit = "g",
        valueExtractor = { weight.inGrams },
        periodExtractor = { Date.from(time) to Date.from(time) }
    )
}

inline fun ActiveCaloriesBurnedRecord.toObservation(): Observation {
    return this.toObservation(
        code = "41981-2",
        display = "Calories burned",
        unit = "kcal",
        valueExtractor = { energy.inCalories },
        periodExtractor = { Date.from(startTime) to Date.from(endTime) }
    )
}

inline fun BloodPressureRecord.toObservation(): Observation {
    val observation = Observation()
    observation.status = Observation.ObservationStatus.FINAL
    observation.code = CodeableConcept().addCoding(
        Coding()
            .setSystem("http://loinc.org")
            .setCode("85354-9")
            .setDisplay("Blood pressure panel with all children optional")
    )

    val period = Period()
    period.start = Date.from(time)
    period.end = Date.from(time)
    observation.effective = period

    val systolicComponent = Observation.ObservationComponentComponent()
    systolicComponent.code = CodeableConcept().addCoding(
        Coding()
            .setSystem("http://loinc.org")
            .setCode("8480-6")
            .setDisplay("Systolic blood pressure")
    )
    systolicComponent.value = Quantity().setValue(systolic.inMillimetersOfMercury).setUnit("mmHg")

    val diastolicComponent = Observation.ObservationComponentComponent()
    diastolicComponent.code = CodeableConcept().addCoding(
        Coding()
            .setSystem("http://loinc.org")
            .setCode("8462-4")
            .setDisplay("Diastolic blood pressure")
    )
    diastolicComponent.value = Quantity().setValue(diastolic.inMillimetersOfMercury).setUnit("mmHg")

    observation.addComponent(systolicComponent)
    observation.addComponent(diastolicComponent)

    return observation
}

inline fun HeightRecord.toObservation(): Observation {
    return this.toObservation(
        code = "8302-2",
        display = "Body height",
        unit = "m",
        valueExtractor = { height.inMeters },
        periodExtractor = { Date.from(time) to Date.from(time) }
    )
}

inline fun HeartRateRecord.toObservations(): List<Observation> {
    return samples.map { sample ->
        val observation = Observation()
        observation.status = Observation.ObservationStatus.FINAL
        observation.code = CodeableConcept().addCoding(
            Coding()
                .setSystem("http://loinc.org")
                .setCode("8867-4")
                .setDisplay("Heart rate")
        )

        val period = Period()
        period.start = Date.from(sample.time)
        period.end = Date.from(sample.time)
        observation.effective = period

        observation.value = Quantity().setValue(sample.beatsPerMinute).setUnit("beats/minute")

        observation
    }
}

inline fun BodyTemperatureRecord.toObservation(): Observation {
    return this.toObservation(
        code = "8310-5",
        display = "Body temperature",
        unit = "°C",
        valueExtractor = { temperature.inCelsius },
        periodExtractor = { Date.from(time) to Date.from(time) }
    )
}