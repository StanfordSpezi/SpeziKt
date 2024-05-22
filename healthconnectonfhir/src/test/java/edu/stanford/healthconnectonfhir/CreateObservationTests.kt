package edu.stanford.healthconnectonfhir

import androidx.health.connect.client.records.ActiveCaloriesBurnedRecord
import androidx.health.connect.client.records.BloodPressureRecord
import androidx.health.connect.client.records.BodyTemperatureRecord
import androidx.health.connect.client.records.HeightRecord
import androidx.health.connect.client.records.WeightRecord
import androidx.health.connect.client.units.Energy
import androidx.health.connect.client.units.Length
import androidx.health.connect.client.units.Mass
import androidx.health.connect.client.units.Pressure
import androidx.health.connect.client.units.Temperature
import org.hl7.fhir.r4.model.Observation
import org.hl7.fhir.r4.model.Period
import org.hl7.fhir.r4.model.Quantity
import org.junit.Test

import org.junit.Assert.*
import java.time.Instant
import java.util.Date

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class CreateObservationTests {
    @Test
    fun stepsRecord_toObservation_isCorrect() {
        val stepsRecord = androidx.health.connect.client.records.StepsRecord(
            count = 1000,
            startTime = Instant.parse("2023-05-18T10:15:30.00Z"),
            endTime = Instant.parse("2023-05-18T11:15:30.00Z"),
            startZoneOffset = null,
            endZoneOffset = null
        )

        val observation = stepsRecord.toObservation()

        assertEquals(Observation.ObservationStatus.FINAL, observation.status)
        assertEquals("55423-8", observation.code.codingFirstRep.code)
        assertEquals(
            Date.from(Instant.parse("2023-05-18T10:15:30.00Z")),
            (observation.effective as Period).start
        )
        assertEquals(
            Date.from(Instant.parse("2023-05-18T11:15:30.00Z")),
            (observation.effective as Period).end
        )
        assertEquals(1000.0, (observation.value as Quantity).value.toDouble(), 0.0)
        assertEquals("steps", (observation.value as Quantity).unit)
    }

    @Test
    fun weightRecord_toObservation_isCorrect() {
        val weightRecord = WeightRecord(
            weight = Mass.kilograms(75.0),
            time = Instant.parse("2023-05-18T10:15:30.00Z"),
            zoneOffset = null
        )

        val observation = weightRecord.toObservation()

        assertEquals(Observation.ObservationStatus.FINAL, observation.status)
        assertEquals("29463-7", observation.code.codingFirstRep.code)
        assertEquals(
            Date.from(Instant.parse("2023-05-18T10:15:30.00Z")),
            (observation.effective as Period).start
        )
        assertEquals(
            Date.from(Instant.parse("2023-05-18T10:15:30.00Z")),
            (observation.effective as Period).end
        )
        assertEquals(75000.0, (observation.value as Quantity).value.toDouble(), 0.0)
        assertEquals("g", (observation.value as Quantity).unit)
    }

    @Test
    fun activeCaloriesBurnedRecord_toObservation_isCorrect() {
        val activeCaloriesBurnedRecord = ActiveCaloriesBurnedRecord(
            energy = Energy.calories(250.0),
            startTime = Instant.parse("2023-05-18T10:15:30.00Z"),
            endTime = Instant.parse("2023-05-18T11:15:30.00Z"),
            startZoneOffset = null,
            endZoneOffset = null
        )

        val observation = activeCaloriesBurnedRecord.toObservation()

        assertEquals(Observation.ObservationStatus.FINAL, observation.status)
        assertEquals("41981-2", observation.code.codingFirstRep.code)
        assertEquals(
            Date.from(Instant.parse("2023-05-18T10:15:30.00Z")),
            (observation.effective as Period).start
        )
        assertEquals(
            Date.from(Instant.parse("2023-05-18T11:15:30.00Z")),
            (observation.effective as Period).end
        )
        assertEquals(250.0, (observation.value as Quantity).value.toDouble(), 0.0)
        assertEquals("kcal", (observation.value as Quantity).unit)
    }

    @Test
    fun bloodPressureRecord_toObservation_isCorrect() {
        val bloodPressureRecord = BloodPressureRecord(
            time = Instant.parse("2023-05-18T10:15:30.00Z"),
            systolic = Pressure.millimetersOfMercury(120.0),
            diastolic = Pressure.millimetersOfMercury(80.0),
            zoneOffset = null
        )

        val observation = bloodPressureRecord.toObservation()

        assertEquals(Observation.ObservationStatus.FINAL, observation.status)
        assertEquals("85354-9", observation.code.codingFirstRep.code)
        assertEquals(
            Date.from(Instant.parse("2023-05-18T10:15:30.00Z")),
            (observation.effective as Period).start
        )
        assertEquals(
            Date.from(Instant.parse("2023-05-18T10:15:30.00Z")),
            (observation.effective as Period).end
        )
        assertEquals(120.0, (observation.component[0].value as Quantity).value.toDouble(), 0.0)
        assertEquals("mmHg", (observation.component[0].value as Quantity).unit)
        assertEquals(80.0, (observation.component[1].value as Quantity).value.toDouble(), 0.0)
        assertEquals("mmHg", (observation.component[1].value as Quantity).unit)
    }

    @Test
    fun heightRecord_toObservation_isCorrect() {
        val heightRecord = HeightRecord(
            time = Instant.parse("2023-05-18T10:15:30.00Z"),
            height = Length.meters(1.75),
            zoneOffset = null
        )

        val observation = heightRecord.toObservation()

        assertEquals(Observation.ObservationStatus.FINAL, observation.status)
        assertEquals("8302-2", observation.code.codingFirstRep.code)
        assertEquals(
            Date.from(Instant.parse("2023-05-18T10:15:30.00Z")),
            (observation.effective as Period).start
        )
        assertEquals(
            Date.from(Instant.parse("2023-05-18T10:15:30.00Z")),
            (observation.effective as Period).end
        )
        assertEquals(1.75, (observation.value as Quantity).value.toDouble(), 0.0)
        assertEquals("m", (observation.value as Quantity).unit)
    }

    @Test
    fun bodyTemperatureRecord_toObservation_isCorrect() {
        val bodyTemperatureRecord = BodyTemperatureRecord(
            time = Instant.parse("2023-05-18T10:15:30.00Z"),
            temperature = Temperature.celsius(37.5),
            zoneOffset = null
        )

        val observation = bodyTemperatureRecord.toObservation()

        assertEquals(Observation.ObservationStatus.FINAL, observation.status)
        assertEquals("8310-5", observation.code.codingFirstRep.code)
        assertEquals(
            Date.from(Instant.parse("2023-05-18T10:15:30.00Z")),
            (observation.effective as Period).start
        )
        assertEquals(
            Date.from(Instant.parse("2023-05-18T10:15:30.00Z")),
            (observation.effective as Period).end
        )
        assertEquals(37.5, (observation.value as Quantity).value.toDouble(), 0.0)
        assertEquals("°C", (observation.value as Quantity).unit)
    }
}