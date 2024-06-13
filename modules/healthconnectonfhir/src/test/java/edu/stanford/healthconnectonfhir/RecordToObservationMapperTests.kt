package edu.stanford.healthconnectonfhir

import androidx.health.connect.client.records.ActiveCaloriesBurnedRecord
import androidx.health.connect.client.records.BloodPressureRecord
import androidx.health.connect.client.records.BodyFatRecord
import androidx.health.connect.client.records.BodyTemperatureRecord
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.records.HeightRecord
import androidx.health.connect.client.records.OxygenSaturationRecord
import androidx.health.connect.client.records.RespiratoryRateRecord
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.records.WeightRecord
import androidx.health.connect.client.records.metadata.Metadata
import androidx.health.connect.client.units.Energy
import androidx.health.connect.client.units.Length
import androidx.health.connect.client.units.Mass
import androidx.health.connect.client.units.Percentage
import androidx.health.connect.client.units.Pressure
import androidx.health.connect.client.units.Temperature
import com.google.common.truth.Truth.assertThat
import org.hl7.fhir.r4.model.DateTimeType
import org.hl7.fhir.r4.model.Observation
import org.hl7.fhir.r4.model.Period
import org.hl7.fhir.r4.model.Quantity
import org.junit.Test
import java.time.Instant
import java.time.ZoneOffset
import java.util.Date

class RecordToObservationMapperTests {
    private var mapper = RecordToObservationMapperImpl()

    @Test
    fun `activeCaloriesBurnedRecord toObservation isCorrect`() {
        val activeCaloriesBurnedRecord = ActiveCaloriesBurnedRecord(
            metadata = Metadata(id = "123456"),
            energy = Energy.calories(250.0),
            startTime = Instant.parse("2023-05-18T10:15:30.00Z"),
            endTime = Instant.parse("2023-05-18T11:15:30.00Z"),
            startZoneOffset = ZoneOffset.UTC,
            endZoneOffset = ZoneOffset.UTC
        )

        val observation = mapper.map(activeCaloriesBurnedRecord).first()

        assertThat(observation.status).isEqualTo(Observation.ObservationStatus.FINAL)
        assertThat(observation.identifier.first().value).isEqualTo("123456")
        assertThat(observation.issued.time).isAtMost(Date().time)
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
            metadata = Metadata(id = "123456"),
            time = Instant.parse("2023-05-18T10:15:30.00Z"),
            systolic = Pressure.millimetersOfMercury(120.0),
            diastolic = Pressure.millimetersOfMercury(80.0),
            zoneOffset = ZoneOffset.UTC
        )

        val observation = mapper.map(bloodPressureRecord).first()

        assertThat(observation.status).isEqualTo(Observation.ObservationStatus.FINAL)
        assertThat(observation.identifier.first().value).isEqualTo("123456")
        assertThat(observation.issued.time).isAtMost(Date().time)
        assertThat(observation.code.codingFirstRep.code).isEqualTo("85354-9")
        assertThat(observation.categoryFirstRep.codingFirstRep.code).isEqualTo("vital-signs")
        assertThat((observation.effective as DateTimeType).value).isEqualTo(Date.from(Instant.parse("2023-05-18T10:15:30.00Z")))
        assertThat((observation.component[0].value as Quantity).value.toDouble()).isEqualTo(120.0)
        assertThat((observation.component[0].value as Quantity).unit).isEqualTo("mmHg")
        assertThat((observation.component[1].value as Quantity).value.toDouble()).isEqualTo(80.0)
        assertThat((observation.component[1].value as Quantity).unit).isEqualTo("mmHg")
    }

    @Test
    fun `bodyFatRecord toObservation isCorrect`() {
        val bodyFatRecord = BodyFatRecord(
            metadata = Metadata(id = "123456"),
            time = Instant.parse("2023-05-18T10:15:30.00Z"),
            percentage = Percentage(10.0),
            zoneOffset = ZoneOffset.UTC
        )

        val observation = mapper.map(bodyFatRecord).first()

        assertThat(observation.status).isEqualTo(Observation.ObservationStatus.FINAL)
        assertThat(observation.identifier.first().value).isEqualTo("123456")
        assertThat(observation.issued.time).isAtMost(Date().time)
        assertThat(observation.code.codingFirstRep.code).isEqualTo("41982-0")
        assertThat((observation.effective as DateTimeType).value).isEqualTo(Date.from(Instant.parse("2023-05-18T10:15:30.00Z")))
        assertThat((observation.value as Quantity).value.toDouble()).isEqualTo(10.0)
        assertThat((observation.value as Quantity).unit).isEqualTo("%")
    }

    @Test
    fun `bodyTemperatureRecord toObservation isCorrect`() {
        val bodyTemperatureRecord = BodyTemperatureRecord(
            metadata = Metadata(id = "123456"),
            time = Instant.parse("2023-05-18T10:15:30.00Z"),
            temperature = Temperature.celsius(37.5),
            zoneOffset = ZoneOffset.UTC
        )

        val observation = mapper.map(bodyTemperatureRecord).first()

        assertThat(observation.status).isEqualTo(Observation.ObservationStatus.FINAL)
        assertThat(observation.identifier.first().value).isEqualTo("123456")
        assertThat(observation.issued.time).isAtMost(Date().time)
        assertThat(observation.code.codingFirstRep.code).isEqualTo("8310-5")
        assertThat(observation.categoryFirstRep.codingFirstRep.code).isEqualTo("vital-signs")
        assertThat((observation.effective as DateTimeType).value).isEqualTo(Date.from(Instant.parse("2023-05-18T10:15:30.00Z")))
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
            assertThat(observation.issued.time).isAtMost(Date().time)
            assertThat(observation.code.codingFirstRep.code).isEqualTo("8867-4")
            assertThat(observation.categoryFirstRep.codingFirstRep.code).isEqualTo("vital-signs")
        }

        assertThat((observations[0].effective as DateTimeType).value).isEqualTo(Date.from(Instant.parse("2023-05-18T10:15:30.00Z")))
        assertThat((observations[0].value as Quantity).value.toDouble()).isEqualTo(72.0)
        assertThat((observations[0].value as Quantity).unit).isEqualTo("beats/minute")

        assertThat((observations[1].effective as DateTimeType).value).isEqualTo(Date.from(Instant.parse("2023-05-18T10:16:30.00Z")))
        assertThat((observations[1].value as Quantity).value.toDouble()).isEqualTo(75.0)
        assertThat((observations[1].value as Quantity).unit).isEqualTo("beats/minute")
    }

    @Test
    fun `heightRecord toObservation isCorrect`() {
        val heightRecord = HeightRecord(
            metadata = Metadata(id = "123456"),
            time = Instant.parse("2023-05-18T10:15:30.00Z"),
            height = Length.meters(1.75),
            zoneOffset = ZoneOffset.UTC
        )

        val observation = mapper.map(heightRecord).first()

        assertThat(observation.status).isEqualTo(Observation.ObservationStatus.FINAL)
        assertThat(observation.identifier.first().value).isEqualTo("123456")
        assertThat(observation.issued.time).isAtMost(Date().time)
        assertThat(observation.code.codingFirstRep.code).isEqualTo("8302-2")
        assertThat(observation.categoryFirstRep.codingFirstRep.code).isEqualTo("vital-signs")
        assertThat((observation.effective as DateTimeType).value).isEqualTo(Date.from(Instant.parse("2023-05-18T10:15:30.00Z")))
        assertThat((observation.value as Quantity).value.toDouble()).isEqualTo(1.75)
        assertThat((observation.value as Quantity).unit).isEqualTo("m")
    }

    @Test
    fun `oxygenSaturationRecord toObservation isCorrect`() {
        val oxygenSaturationRecord = OxygenSaturationRecord(
            metadata = Metadata(id = "123456"),
            time = Instant.parse("2023-05-18T10:15:30.00Z"),
            percentage = Percentage(99.0),
            zoneOffset = ZoneOffset.UTC
        )

        val observation = mapper.map(oxygenSaturationRecord).first()

        assertThat(observation.status).isEqualTo(Observation.ObservationStatus.FINAL)
        assertThat(observation.identifier.first().value).isEqualTo("123456")
        assertThat(observation.issued.time).isAtMost(Date().time)
        assertThat(observation.categoryFirstRep.codingFirstRep.code).isEqualTo("vital-signs")
        assertThat(observation.code.codingFirstRep.code).isEqualTo("59408-5")
        assertThat((observation.effective as DateTimeType).value).isEqualTo(Date.from(Instant.parse("2023-05-18T10:15:30.00Z")))
        assertThat((observation.value as Quantity).value.toDouble()).isEqualTo(99.0)
        assertThat((observation.value as Quantity).unit).isEqualTo("%")
    }

    @Test
    fun `respiratoryRate toObservation isCorrect`() {
        val respiratoryRateRecord = RespiratoryRateRecord(
            metadata = Metadata(id = "123456"),
            time = Instant.parse("2023-05-18T10:15:30.00Z"),
            rate = 18.0,
            zoneOffset = ZoneOffset.UTC
        )

        val observation = mapper.map(respiratoryRateRecord).first()

        assertThat(observation.status).isEqualTo(Observation.ObservationStatus.FINAL)
        assertThat(observation.identifier.first().value).isEqualTo("123456")
        assertThat(observation.issued.time).isAtMost(Date().time)
        assertThat(observation.categoryFirstRep.codingFirstRep.code).isEqualTo("vital-signs")
        assertThat(observation.code.codingFirstRep.code).isEqualTo("9279-1")
        assertThat((observation.effective as DateTimeType).value).isEqualTo(Date.from(Instant.parse("2023-05-18T10:15:30.00Z")))
        assertThat((observation.value as Quantity).value.toDouble()).isEqualTo(18.0)
        assertThat((observation.value as Quantity).unit).isEqualTo("/min")
    }

    @Test
    fun `stepsRecord toObservation isCorrect`() {
        val stepsRecord = StepsRecord(
            metadata = Metadata(id = "123456"),
            count = 1000,
            startTime = Instant.parse("2023-05-18T10:15:30.00Z"),
            endTime = Instant.parse("2023-05-18T11:15:30.00Z"),
            startZoneOffset = ZoneOffset.UTC,
            endZoneOffset = ZoneOffset.UTC
        )

        val observation = mapper.map(stepsRecord).first()

        assertThat(observation.status).isEqualTo(Observation.ObservationStatus.FINAL)
        assertThat(observation.identifier.first().value).isEqualTo("123456")
        assertThat(observation.issued.time).isAtMost(Date().time)
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
            metadata = Metadata(id = "123456"),
            weight = Mass.kilograms(75.0),
            time = Instant.parse("2023-05-18T10:15:30.00Z"),
            zoneOffset = ZoneOffset.UTC
        )

        val observation = mapper.map(weightRecord).first()

        assertThat(observation.status).isEqualTo(Observation.ObservationStatus.FINAL)
        assertThat(observation.identifier.first().value).isEqualTo("123456")
        assertThat(observation.issued.time).isAtMost(Date().time)
        assertThat(observation.code.codingFirstRep.code).isEqualTo("29463-7")
        assertThat(observation.categoryFirstRep.codingFirstRep.code).isEqualTo("vital-signs")
        assertThat((observation.effective as DateTimeType).value).isEqualTo(Date.from(Instant.parse("2023-05-18T10:15:30.00Z")))
        assertThat((observation.value as Quantity).value.toDouble()).isEqualTo(75000.0)
        assertThat((observation.value as Quantity).unit).isEqualTo("g")
    }
}
