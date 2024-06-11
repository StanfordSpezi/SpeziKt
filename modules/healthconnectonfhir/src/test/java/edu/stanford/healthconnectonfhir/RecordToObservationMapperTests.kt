package edu.stanford.healthconnectonfhir

import androidx.health.connect.client.records.ActiveCaloriesBurnedRecord
import androidx.health.connect.client.records.BloodPressureRecord
import androidx.health.connect.client.records.BodyTemperatureRecord
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.records.HeightRecord
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.records.WeightRecord
import androidx.health.connect.client.units.Energy
import androidx.health.connect.client.units.Length
import androidx.health.connect.client.units.Mass
import androidx.health.connect.client.units.Pressure
import androidx.health.connect.client.units.Temperature
import com.google.common.truth.Truth.assertThat
import org.hl7.fhir.r4.model.Observation
import org.hl7.fhir.r4.model.Period
import org.hl7.fhir.r4.model.Quantity
import org.junit.Before
import org.junit.Test
import java.time.Instant
import java.time.ZoneOffset
import java.util.Date

class RecordToObservationMapperTests {
    private lateinit var mapper: RecordToObservationMapper

    @Before
    fun setup() {
        mapper = RecordToObservationMapperImpl()
    }

    @Test
    fun `stepsRecord toObservation isCorrect`() {
        val stepsRecord = StepsRecord(
            count = 1000,
            startTime = Instant.parse("2023-05-18T10:15:30.00Z"),
            endTime = Instant.parse("2023-05-18T11:15:30.00Z"),
            startZoneOffset = ZoneOffset.UTC,
            endZoneOffset = ZoneOffset.UTC
        )

        val observation = mapper.map(stepsRecord).first()

        assertThat(observation.status).isEqualTo(Observation.ObservationStatus.FINAL)
        assertThat(observation.code.codingFirstRep.code).isEqualTo("55423-8")
        assertThat(observation.categoryFirstRep.codingFirstRep.code).isEqualTo("activity")
        assertThat((observation.effective as Period).start).isEqualTo(Date.from(Instant.parse("2023-05-18T10:15:30.00Z")))
        assertThat((observation.effective as Period).end).isEqualTo(Date.from(Instant.parse("2023-05-18T11:15:30.00Z")))
        assertThat((observation.value as Quantity).value.toDouble()).isEqualTo(1000.0)
        assertThat((observation.value as Quantity).unit).isEqualTo("steps")
    }

    @Test
    fun `weightRecord toObservation isCorrect`() {
        val weightRecord = WeightRecord(
            weight = Mass.kilograms(75.0),
            time = Instant.parse("2023-05-18T10:15:30.00Z"),
            zoneOffset = ZoneOffset.UTC
        )

        val observation = mapper.map(weightRecord).first()

        assertThat(observation.status).isEqualTo(Observation.ObservationStatus.FINAL)
        assertThat(observation.code.codingFirstRep.code).isEqualTo("29463-7")
        assertThat(observation.categoryFirstRep.codingFirstRep.code).isEqualTo("vital-signs")
        assertThat((observation.effective as Period).start).isEqualTo(Date.from(Instant.parse("2023-05-18T10:15:30.00Z")))
        assertThat((observation.effective as Period).end).isEqualTo(Date.from(Instant.parse("2023-05-18T10:15:30.00Z")))
        assertThat((observation.value as Quantity).value.toDouble()).isEqualTo(75000.0)
        assertThat((observation.value as Quantity).unit).isEqualTo("g")
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

        val observation = mapper.map(activeCaloriesBurnedRecord).first()

        assertThat(observation.status).isEqualTo(Observation.ObservationStatus.FINAL)
        assertThat(observation.code.codingFirstRep.code).isEqualTo("41981-2")
        assertThat(observation.categoryFirstRep.codingFirstRep.code).isEqualTo("activity")
        assertThat((observation.effective as Period).start).isEqualTo(Date.from(Instant.parse("2023-05-18T10:15:30.00Z")))
        assertThat((observation.effective as Period).end).isEqualTo(Date.from(Instant.parse("2023-05-18T11:15:30.00Z")))
        assertThat((observation.value as Quantity).value.toDouble()).isEqualTo(250.0)
        assertThat((observation.value as Quantity).unit).isEqualTo("kcal")
    }

    @Test
    fun `bloodPressureRecord toObservation isCorrect`() {
        val bloodPressureRecord = BloodPressureRecord(
            time = Instant.parse("2023-05-18T10:15:30.00Z"),
            systolic = Pressure.millimetersOfMercury(120.0),
            diastolic = Pressure.millimetersOfMercury(80.0),
            zoneOffset = ZoneOffset.UTC
        )

        val observation = mapper.map(bloodPressureRecord).first()

        assertThat(observation.status).isEqualTo(Observation.ObservationStatus.FINAL)
        assertThat(observation.code.codingFirstRep.code).isEqualTo("85354-9")
        assertThat(observation.categoryFirstRep.codingFirstRep.code).isEqualTo("vital-signs")
        assertThat((observation.effective as Period).start).isEqualTo(Date.from(Instant.parse("2023-05-18T10:15:30.00Z")))
        assertThat((observation.effective as Period).end).isEqualTo(Date.from(Instant.parse("2023-05-18T10:15:30.00Z")))
        assertThat((observation.component[0].value as Quantity).value.toDouble()).isEqualTo(120.0)
        assertThat((observation.component[0].value as Quantity).unit).isEqualTo("mmHg")
        assertThat((observation.component[1].value as Quantity).value.toDouble()).isEqualTo(80.0)
        assertThat((observation.component[1].value as Quantity).unit).isEqualTo("mmHg")
    }

    @Test
    fun `heightRecord toObservation isCorrect`() {
        val heightRecord = HeightRecord(
            time = Instant.parse("2023-05-18T10:15:30.00Z"),
            height = Length.meters(1.75),
            zoneOffset = ZoneOffset.UTC
        )

        val observation = mapper.map(heightRecord).first()

        assertThat(observation.status).isEqualTo(Observation.ObservationStatus.FINAL)
        assertThat(observation.code.codingFirstRep.code).isEqualTo("8302-2")
        assertThat(observation.categoryFirstRep.codingFirstRep.code).isEqualTo("vital-signs")
        assertThat((observation.effective as Period).start).isEqualTo(Date.from(Instant.parse("2023-05-18T10:15:30.00Z")))
        assertThat((observation.effective as Period).end).isEqualTo(Date.from(Instant.parse("2023-05-18T10:15:30.00Z")))
        assertThat((observation.value as Quantity).value.toDouble()).isEqualTo(1.75)
        assertThat((observation.value as Quantity).unit).isEqualTo("m")
    }

    @Test
    fun `bodyTemperatureRecord toObservation isCorrect`() {
        val bodyTemperatureRecord = BodyTemperatureRecord(
            time = Instant.parse("2023-05-18T10:15:30.00Z"),
            temperature = Temperature.celsius(37.5),
            zoneOffset = ZoneOffset.UTC
        )

        val observation = mapper.map(bodyTemperatureRecord).first()

        assertThat(observation.status).isEqualTo(Observation.ObservationStatus.FINAL)
        assertThat(observation.code.codingFirstRep.code).isEqualTo("8310-5")
        assertThat(observation.categoryFirstRep.codingFirstRep.code).isEqualTo("vital-signs")
        assertThat((observation.effective as Period).start).isEqualTo(Date.from(Instant.parse("2023-05-18T10:15:30.00Z")))
        assertThat((observation.effective as Period).end).isEqualTo(Date.from(Instant.parse("2023-05-18T10:15:30.00Z")))
        assertThat((observation.value as Quantity).value.toDouble()).isEqualTo(37.5)
        assertThat((observation.value as Quantity).unit).isEqualTo("°C")
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

        val observations = mapper.map(heartRateRecord)

        observations.forEach { observation ->
            assertThat(observation.status).isEqualTo(Observation.ObservationStatus.FINAL)
            assertThat(observation.code.codingFirstRep.code).isEqualTo("8867-4")
            assertThat(observation.categoryFirstRep.codingFirstRep.code).isEqualTo("vital-signs")
        }

        assertThat((observations[0].effective as Period).start).isEqualTo(Date.from(Instant.parse("2023-05-18T10:15:30.00Z")))
        assertThat((observations[0].effective as Period).end).isEqualTo(Date.from(Instant.parse("2023-05-18T10:15:30.00Z")))
        assertThat((observations[0].value as Quantity).value.toDouble()).isEqualTo(72.0)
        assertThat((observations[0].value as Quantity).unit).isEqualTo("beats/minute")

        assertThat((observations[1].effective as Period).start).isEqualTo(Date.from(Instant.parse("2023-05-18T10:16:30.00Z")))
        assertThat((observations[1].effective as Period).end).isEqualTo(Date.from(Instant.parse("2023-05-18T10:16:30.00Z")))
        assertThat((observations[1].value as Quantity).value.toDouble()).isEqualTo(75.0)
        assertThat((observations[1].value as Quantity).unit).isEqualTo("beats/minute")
    }
}
