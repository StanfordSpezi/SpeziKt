package edu.stanford.bdh.engagehf.bluetooth.data.mapper

import android.bluetooth.BluetoothDevice
import androidx.health.connect.client.records.BloodPressureRecord
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.records.WeightRecord
import com.google.common.truth.Truth.assertThat
import edu.stanford.bdh.engagehf.R
import edu.stanford.bdh.engagehf.bluetooth.component.OperationStatus
import edu.stanford.bdh.engagehf.bluetooth.data.models.Action
import edu.stanford.bdh.engagehf.bluetooth.data.models.BluetoothUiState
import edu.stanford.bdh.engagehf.bluetooth.data.models.DeviceUiModel
import edu.stanford.bdh.engagehf.bluetooth.service.BLEDeviceSession
import edu.stanford.bdh.engagehf.bluetooth.service.EngageBLEServiceState
import edu.stanford.bdh.engagehf.bluetooth.service.Measurement
import edu.stanford.spezi.core.utils.LocaleProvider
import io.mockk.every
import io.mockk.mockk
import org.junit.Test
import java.time.Instant
import java.time.ZoneOffset
import java.util.Locale

class BluetoothUiStateMapperTest {
    private val localeProvider: LocaleProvider = mockk {
        every { getDefaultLocale() } returns Locale.US
    }

    private val messageActionMapper = mockk<MessageActionMapper>()

    private val mapper = BluetoothUiStateMapper(
        localeProvider = localeProvider,
        messageActionMapper = messageActionMapper
    )
    private val bloodPressure: Measurement.BloodPressure = mockk {
        every { systolic } returns SYSTOLIC.toFloat()
        every { diastolic } returns DIASTOLIC.toFloat()
        every { pulseRate } returns PULSE_RATE
    }
    private val weight: Measurement.Weight = mockk {
        every { weight } returns WEIGHT
    }

    private val device: BluetoothDevice = mockk {
        every { address } returns ADDRESS
    }

    @Test
    fun `it should map idle state correctly`() {
        // given
        val state = EngageBLEServiceState.Idle

        // when
        val result = mapper.mapBleServiceState(state)

        // then
        assertThat(result).isEqualTo(
            BluetoothUiState.Idle(
                description = R.string.bluetooth_initializing_description,
            )
        )
    }

    @Test
    fun `it should map BluetoothNotEnabled state correctly`() {
        // given
        val state = EngageBLEServiceState.BluetoothNotEnabled

        // when
        val result = mapper.mapBleServiceState(state)

        // then
        assertThat(result).isEqualTo(
            BluetoothUiState.Idle(
                description = R.string.bluetooth_not_enabled_description,
                action = Action.Settings.BluetoothSettings,
            )
        )
    }

    @Test
    fun `it should map MissingPermissions state correctly`() {
        // given
        val permissions = listOf("permission1", "permission2")
        val state = EngageBLEServiceState.MissingPermissions(permissions)

        // when
        val result = mapper.mapBleServiceState(state) as BluetoothUiState.Idle

        // then
        assertThat(result).isEqualTo(
            BluetoothUiState.Idle(
                description = R.string.bluetooth_permissions_not_granted_description,
                missingPermissions = permissions,
                action = Action.Settings.AppSettings,
            )
        )
    }

    @Test
    fun `it should map empty sessions correctly`() {
        // given
        val state = EngageBLEServiceState.Scanning(sessions = emptyList())

        // when
        val result = mapper.mapBleServiceState(state) as BluetoothUiState.Ready

        // then
        with(result) {
            assertThat(header).isEqualTo(R.string.paired_devices_hint_description)
            assertThat(devices).isEmpty()
        }
    }

    @Test
    fun `it should map BloodPressure correctly`() {
        // given
        val session = BLEDeviceSession(device = device, measurements = listOf(bloodPressure))
        val state = EngageBLEServiceState.Scanning(sessions = listOf(session))
        val expectedDevice = DeviceUiModel(
            address = ADDRESS,
            measurementsCount = session.measurements.size,
            summary = "Blood Pressure: $SYSTOLIC mmHg / $DIASTOLIC mmHg"
        )

        // when
        val result = mapper.mapBleServiceState(state) as BluetoothUiState.Ready

        // then
        with(result) {
            assertThat(header).isEqualTo(null)
            assertThat(devices.first()).isEqualTo(expectedDevice)
        }
    }

    @Test
    fun `it should map Weight correctly`() {
        // given
        val session = BLEDeviceSession(device = device, measurements = listOf(weight))
        val state = EngageBLEServiceState.Scanning(sessions = listOf(session))
        val expectedDevice = DeviceUiModel(
            address = ADDRESS,
            measurementsCount = session.measurements.size,
            summary = "Weight: 10.05 lbs"
        )

        // when
        val result = mapper.mapBleServiceState(state) as BluetoothUiState.Ready

        // then
        with(result) {
            assertThat(header).isEqualTo(null)
            assertThat(devices.first()).isEqualTo(expectedDevice)
        }
    }

    @Test
    fun `it should map measurementDialog ui state of weight in US correctly`() {
        // given
        every { localeProvider.getDefaultLocale() } returns Locale.US
        val measurement = weight

        // when
        val result = mapper.mapMeasurementDialog(measurement = measurement)

        // then
        with(result) {
            assertThat(this.measurement).isEqualTo(measurement)
            assertThat(isVisible).isTrue()
            assertThat(isProcessing).isFalse()
            assertThat(formattedWeight).isEqualTo("10.05 lbs")
        }
    }

    @Test
    fun `it should map measurementDialog ui state of weight in DE correctly`() {
        // given
        every { localeProvider.getDefaultLocale() } returns Locale.GERMAN
        val measurement = weight

        // when
        val result = mapper.mapMeasurementDialog(measurement = measurement)

        // then
        with(result) {
            assertThat(this.measurement).isEqualTo(measurement)
            assertThat(isVisible).isTrue()
            assertThat(isProcessing).isFalse()
            assertThat(formattedWeight).isEqualTo("4,56 kg")
        }
    }

    @Test
    fun `it should map measurementDialog ui state of blood pressure correctly`() {
        // given
        val measurement = bloodPressure

        // when
        val result = mapper.mapMeasurementDialog(measurement = measurement)

        // then
        with(result) {
            assertThat(this.measurement).isEqualTo(measurement)
            assertThat(isVisible).isTrue()
            assertThat(isProcessing).isFalse()
            assertThat(formattedSystolic).isEqualTo("$SYSTOLIC mmHg")
            assertThat(formattedDiastolic).isEqualTo("$DIASTOLIC mmHg")
            assertThat(formattedHeartRate).isEqualTo("${PULSE_RATE.toInt()} bpm")
        }
    }

    @Test
    fun `it should map blood pressure correctly when result is success`() {
        // given
        val bloodPressureRecord: BloodPressureRecord = mockk {
            every { systolic.inMillimetersOfMercury } returns 120.0
            every { diastolic.inMillimetersOfMercury } returns 80.0
            every { time } returns Instant.now()
            every { zoneOffset } returns ZoneOffset.UTC
        }

        // when
        val result = mapper.mapBloodPressure(Result.success(bloodPressureRecord))

        // then
        with(result) {
            assertThat(title).isEqualTo("Blood Pressure")
            assertThat(status).isEqualTo(OperationStatus.SUCCESS)
            assertThat(value).isEqualTo("120.0/80.0")
            assertThat(unit).isEqualTo("mmHg")
        }
    }

    @Test
    fun `it should map blood pressure correctly when result is failure`() {
        // given
        val result: Result<BloodPressureRecord?> = Result.failure(Throwable())

        // when
        val vitalDisplayData = mapper.mapBloodPressure(result)

        // then
        with(vitalDisplayData) {
            assertThat(title).isEqualTo("Blood Pressure")
            assertThat(status).isEqualTo(OperationStatus.FAILURE)
            assertThat(value).isNull()
            assertThat(unit).isNull()
        }
    }

    @Test
    fun `it should map blood pressure correctly when result is no data`() {
        // given
        val result: Result<BloodPressureRecord?> = Result.success(null)

        // when
        val vitalDisplayData = mapper.mapBloodPressure(result)

        // then
        with(vitalDisplayData) {
            assertThat(title).isEqualTo("Blood Pressure")
            assertThat(status).isEqualTo(OperationStatus.NO_DATA)
            assertThat(value).isEqualTo("No data available")
            assertThat(unit).isNull()
        }
    }

    @Test
    fun `it should map weight correctly for US locale`() {
        // given
        val weightRecord: WeightRecord = mockk {
            every { weight.inPounds } returns 154.32
            every { time } returns Instant.now()
            every { zoneOffset } returns ZoneOffset.UTC
        }
        val result = Result.success(weightRecord)
        every { localeProvider.getDefaultLocale() } returns Locale.US

        // when
        val vitalDisplayData = mapper.mapWeight(result)

        // then
        with(vitalDisplayData) {
            assertThat(title).isEqualTo("Weight")
            assertThat(status).isEqualTo(OperationStatus.SUCCESS)
            assertThat(value).isEqualTo("154.32")
            assertThat(unit).isEqualTo("lbs")
        }
    }

    @Test
    fun `it should map weight correctly for German locale`() {
        // given
        val weightRecord: WeightRecord = mockk {
            every { weight.inKilograms } returns 70.12
            every { time } returns Instant.now()
            every { zoneOffset } returns ZoneOffset.UTC
        }
        val result = Result.success(weightRecord)
        every { localeProvider.getDefaultLocale() } returns Locale.GERMANY

        // when
        val vitalDisplayData = mapper.mapWeight(result)

        // then
        with(vitalDisplayData) {
            assertThat(title).isEqualTo("Weight")
            assertThat(status).isEqualTo(OperationStatus.SUCCESS)
            assertThat(value).isEqualTo("70,12")
            assertThat(unit).isEqualTo("kg")
        }
    }

    @Test
    fun `it should map weight correctly when result is failure`() {
        // given
        val result: Result<WeightRecord?> = Result.failure(Throwable())

        // when
        val vitalDisplayData = mapper.mapWeight(result)

        // then
        with(vitalDisplayData) {
            assertThat(title).isEqualTo("Weight")
            assertThat(status).isEqualTo(OperationStatus.FAILURE)
            assertThat(value).isNull()
            assertThat(unit).isNull()
        }
    }

    @Test
    fun `it should map weight correctly when result is no data`() {
        // given
        val result: Result<WeightRecord?> = Result.success(null)

        // when
        val vitalDisplayData = mapper.mapWeight(result)

        // then
        with(vitalDisplayData) {
            assertThat(title).isEqualTo("Weight")
            assertThat(status).isEqualTo(OperationStatus.NO_DATA)
            assertThat(value).isEqualTo("No data available")
            assertThat(unit).isNull()
        }
    }

    @Test
    fun `it should map heart rate correctly for success case`() {
        // given
        val beatsPerMinute = 72L
        val instant = Instant.parse("2024-01-01T15:00:00Z")
        val heartRateRecord: HeartRateRecord = mockk {
            every { samples } returns listOf(
                HeartRateRecord.Sample(instant, beatsPerMinute)
            )
            every { startZoneOffset } returns ZoneOffset.UTC
        }
        val result = Result.success(heartRateRecord)

        // when
        every { localeProvider.getDefaultLocale() } returns Locale.US
        val vitalDisplayData = mapper.mapHeartRate(result)

        // then
        with(vitalDisplayData) {
            assertThat(title).isEqualTo("Heart Rate")
            assertThat(value).isEqualTo("$beatsPerMinute.0")
            assertThat(unit).isEqualTo("bpm")
            assertThat(date).isEqualTo("01.01.2024, 15:00")
        }
    }

    @Test
    fun `it should map heart rate correctly on failure`() {
        // given
        val result: Result<HeartRateRecord?> = Result.failure(Throwable())

        // when
        val vitalDisplayData = mapper.mapHeartRate(result)

        // then
        with(vitalDisplayData) {
            assertThat(title).isEqualTo("Heart Rate")
            assertThat(status).isEqualTo(OperationStatus.FAILURE)
            assertThat(value).isNull()
            assertThat(unit).isNull()
        }
    }

    @Test
    fun `it should map heart rate correctly when value is null`() {
        // given
        val result: Result<HeartRateRecord?> = Result.success(null)

        // when
        val vitalDisplayData = mapper.mapHeartRate(result)

        // then
        with(vitalDisplayData) {
            assertThat(title).isEqualTo("Heart Rate")
            assertThat(status).isEqualTo(OperationStatus.NO_DATA)
            assertThat(value).isEqualTo("No data available")
            assertThat(unit).isNull()
        }
    }

    private companion object {
        const val SYSTOLIC = 1
        const val DIASTOLIC = 3
        const val PULSE_RATE = 4.32f
        const val WEIGHT = 4.56
        const val ADDRESS = "some device address"
    }
}
