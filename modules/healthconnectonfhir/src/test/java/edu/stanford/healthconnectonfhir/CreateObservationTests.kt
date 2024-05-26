package edu.stanford.healthconnectonfhir

import androidx.health.connect.client.records.ActiveCaloriesBurnedRecord
import androidx.health.connect.client.records.BloodPressureRecord
import androidx.health.connect.client.records.BodyTemperatureRecord
import androidx.health.connect.client.records.HeightRecord
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.records.WeightRecord
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.units.Energy
import androidx.health.connect.client.units.Length
import androidx.health.connect.client.units.Mass
import androidx.health.connect.client.units.Pressure
import androidx.health.connect.client.units.Temperature
import ca.uhn.fhir.context.FhirContext
import org.hl7.fhir.r4.model.Observation
import org.hl7.fhir.r4.model.Period
import org.hl7.fhir.r4.model.Quantity
import org.junit.Test

import org.junit.Assert.*
import java.time.Instant
import java.time.ZoneOffset
import java.util.Date

class CreateObservationTests {
    private val fhirContext: FhirContext = FhirContext.forR4()
    @Test
    fun `stepsRecord toObservation isCorrect`() {
        val stepsRecord = StepsRecord(
            count = 1000,
            startTime = Instant.parse("2023-05-18T10:15:30.00Z"),
            endTime = Instant.parse("2023-05-18T11:15:30.00Z"),
            startZoneOffset = ZoneOffset.UTC,
            endZoneOffset = ZoneOffset.UTC
        )

        val observation = stepsRecord.toObservation()

        assertEquals(Observation.ObservationStatus.FINAL, observation.status)
        assertEquals("55423-8", observation.code.codingFirstRep.code)
        assertEquals("activity", observation.categoryFirstRep.codingFirstRep.code)
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
    fun `weightRecord toObservation isCorrect`() {
        val weightRecord = WeightRecord(
            weight = Mass.kilograms(75.0),
            time = Instant.parse("2023-05-18T10:15:30.00Z"),
            zoneOffset = ZoneOffset.UTC
        )

        val observation = weightRecord.toObservation()

        assertEquals(Observation.ObservationStatus.FINAL, observation.status)
        assertEquals("29463-7", observation.code.codingFirstRep.code)
        assertEquals("vital-signs", observation.categoryFirstRep.codingFirstRep.code)
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
    fun `activeCaloriesBurnedRecord toObservation isCorrect`() {
        val activeCaloriesBurnedRecord = ActiveCaloriesBurnedRecord(
            energy = Energy.calories(250.0),
            startTime = Instant.parse("2023-05-18T10:15:30.00Z"),
            endTime = Instant.parse("2023-05-18T11:15:30.00Z"),
            startZoneOffset = ZoneOffset.UTC,
            endZoneOffset = ZoneOffset.UTC
        )

        val observation = activeCaloriesBurnedRecord.toObservation()

        assertEquals(Observation.ObservationStatus.FINAL, observation.status)
        assertEquals("41981-2", observation.code.codingFirstRep.code)
        assertEquals("activity", observation.categoryFirstRep.codingFirstRep.code)
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
    fun `bloodPressureRecord toObservation isCorrect`() {
        val bloodPressureRecord = BloodPressureRecord(
            time = Instant.parse("2023-05-18T10:15:30.00Z"),
            systolic = Pressure.millimetersOfMercury(120.0),
            diastolic = Pressure.millimetersOfMercury(80.0),
            zoneOffset = ZoneOffset.UTC
        )

        val observation = bloodPressureRecord.toObservation()

        assertEquals(Observation.ObservationStatus.FINAL, observation.status)
        assertEquals("85354-9", observation.code.codingFirstRep.code)
        assertEquals("vital-signs", observation.categoryFirstRep.codingFirstRep.code)
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
    fun `heightRecord toObservation isCorrect`() {
        val heightRecord = HeightRecord(
            time = Instant.parse("2023-05-18T10:15:30.00Z"),
            height = Length.meters(1.75),
            zoneOffset = ZoneOffset.UTC
        )

        val observation = heightRecord.toObservation()

        assertEquals(Observation.ObservationStatus.FINAL, observation.status)
        assertEquals("8302-2", observation.code.codingFirstRep.code)
        assertEquals("vital-signs", observation.categoryFirstRep.codingFirstRep.code)
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
    fun `bodyTemperatureRecord toObservation isCorrect`() {
        val bodyTemperatureRecord = BodyTemperatureRecord(
            time = Instant.parse("2023-05-18T10:15:30.00Z"),
            temperature = Temperature.celsius(37.5),
            zoneOffset = ZoneOffset.UTC
        )

        val observation = bodyTemperatureRecord.toObservation()

        assertEquals(Observation.ObservationStatus.FINAL, observation.status)
        assertEquals("8310-5", observation.code.codingFirstRep.code)
        assertEquals("vital-signs", observation.categoryFirstRep.codingFirstRep.code)
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

    @Test
    fun `heartRateRecord toObservations isCorrect`() {
        val heartRateRecord = HeartRateRecord(
            samples = listOf(
                HeartRateRecord.Sample(
                    time = Instant.parse("2023-05-18T10:15:30.00Z"),
                    beatsPerMinute = 72L
                ),
                HeartRateRecord.Sample(
                    time = Instant.parse("2023-05-18T10:16:30.00Z"),
                    beatsPerMinute = 75L
                )
            ),
            startTime = Instant.parse("2023-05-18T10:15:30.00Z"),
            endTime = Instant.parse("2023-05-18T10:17:30.00Z"),
            startZoneOffset = ZoneOffset.UTC,
            endZoneOffset = ZoneOffset.UTC
        )

        val observations = heartRateRecord.toObservations()

        observations.forEach { observation ->
            assertEquals(Observation.ObservationStatus.FINAL, observation.status)
            assertEquals("8867-4", observation.code.codingFirstRep.code)
            assertEquals("vital-signs", observation.categoryFirstRep.codingFirstRep.code)
        }

        assertEquals(
            Date.from(Instant.parse("2023-05-18T10:15:30.00Z")),
            (observations[0].effective as Period).start
        )
        assertEquals(
            Date.from(Instant.parse("2023-05-18T10:15:30.00Z")),
            (observations[0].effective as Period).end
        )
        assertEquals(72.0, (observations[0].value as Quantity).value.toDouble(), 0.0)
        assertEquals("beats/minute", (observations[0].value as Quantity).unit)

        assertEquals(
            Date.from(Instant.parse("2023-05-18T10:16:30.00Z")),
            (observations[1].effective as Period).start
        )
        assertEquals(
            Date.from(Instant.parse("2023-05-18T10:16:30.00Z")),
            (observations[1].effective as Period).end
        )
        assertEquals(75.0, (observations[1].value as Quantity).value.toDouble(), 0.0)
        assertEquals("beats/minute", (observations[1].value as Quantity).unit)
    }
}