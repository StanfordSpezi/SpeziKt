package edu.stanford.spezi.app.bluetooth.data.mapper

import android.bluetooth.BluetoothDevice
import com.google.common.truth.Truth.assertThat
import edu.stanford.spezi.app.bluetooth.data.models.DeviceUiModel
import edu.stanford.spezi.core.bluetooth.data.model.BLEDeviceSession
import edu.stanford.spezi.core.bluetooth.data.model.BLEServiceState
import edu.stanford.spezi.core.bluetooth.data.model.Measurement
import io.mockk.every
import io.mockk.mockk
import org.junit.Test

class BluetoothUiStateMapperTest {
    private val mapper = BluetoothUiStateMapper()
    private val bloodPressure: Measurement.BloodPressure = mockk {
        every { systolic } returns SYSTOLIC
        every { diastolic } returns DIASTOLIC
    }
    private val weight: Measurement.Weight = mockk {
        every { weight } returns WEIGHT
    }

    private val device: BluetoothDevice = mockk {
        every { address } returns ADDRESS
    }

    @Test
    fun `it should map empty sessions correctly`() {
        // given
        val state = BLEServiceState.Scanning(sessions = emptyList())

        // when
        val result = mapper.map(state)

        // then
        with(result) {
            assertThat(header).isEqualTo("No devices connected yet")
            assertThat(devices).isEmpty()
        }
    }

    @Test
    fun `it should map BloodPressure correctly`() {
        // given
        val session = BLEDeviceSession(device = device, measurements = listOf(bloodPressure))
        val state = BLEServiceState.Scanning(sessions = listOf(session))
        val expectedDevice = DeviceUiModel(
            address = ADDRESS,
            measurementsCount = session.measurements.size,
            summary = "Blood Pressure: $SYSTOLIC / $DIASTOLIC"
        )

        // when
        val result = mapper.map(state)

        // then
        with(result) {
            assertThat(header).isEqualTo("Connected devices (1)")
            assertThat(devices.first()).isEqualTo(expectedDevice)
        }
    }

    @Test
    fun `it should map Weight correctly`() {
        // given
        val session = BLEDeviceSession(device = device, measurements = listOf(weight))
        val state = BLEServiceState.Scanning(sessions = listOf(session))
        val expectedDevice = DeviceUiModel(
            address = ADDRESS,
            measurementsCount = session.measurements.size,
            summary = "Weight: $WEIGHT"
        )

        // when
        val result = mapper.map(state)

        // then
        with(result) {
            assertThat(header).isEqualTo("Connected devices (1)")
            assertThat(devices.first()).isEqualTo(expectedDevice)
        }
    }

    private companion object {
        const val SYSTOLIC = 1.23f
        const val DIASTOLIC = 3.21f
        const val WEIGHT = 4.56
        const val ADDRESS = "some device address"
    }
}
