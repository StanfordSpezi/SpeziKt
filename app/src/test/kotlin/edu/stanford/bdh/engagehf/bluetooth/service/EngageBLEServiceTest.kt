package edu.stanford.bdh.engagehf.bluetooth.service

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothGattService
import com.google.common.truth.Truth.assertThat
import edu.stanford.bdh.engagehf.bluetooth.service.mapper.MeasurementMapper
import edu.stanford.spezi.core.bluetooth.api.BLEService
import edu.stanford.spezi.core.bluetooth.data.model.BLEDevice
import edu.stanford.spezi.core.bluetooth.data.model.BLEServiceEvent
import edu.stanford.spezi.core.bluetooth.data.model.BLEServiceState
import edu.stanford.spezi.core.testing.SpeziTestScope
import edu.stanford.spezi.core.testing.runTestUnconfined
import edu.stanford.spezi.core.utils.UUID
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import org.junit.Before
import org.junit.Test

class EngageBLEServiceTest {
    private val measurementMapper: MeasurementMapper = mockk(relaxed = true)
    private val bleService: BLEService = mockk(relaxed = true)
    private val bleServiceState = MutableStateFlow<BLEServiceState>(BLEServiceState.Idle)
    private val bleServiceEvents = MutableSharedFlow<BLEServiceEvent>()
    private val device: BLEDevice = mockk {
        every { address } returns "some device name"
    }

    private val service by lazy {
        EngageBLEService(
            bleService = bleService,
            measurementMapper = measurementMapper,
            ioScope = SpeziTestScope()
        )
    }

    @Before
    fun setup() {
        every { bleService.state } returns bleServiceState
        every { bleService.events } returns bleServiceEvents
    }

    @Test
    fun `it should handle start correctly`() {
        // when
        start()

        // then
        verify {
            bleService.state
            bleService.events
            bleService.startDiscovering(
                services = BLEServiceType.entries.map { it.service },
                autoConnect = true
            )
        }
    }

    @Test
    fun `it should handle idle state correctly`() {
        // given
        val state = BLEServiceState.Idle
        start()

        // when
        bleServiceState.value = state

        // then
        assertState(state = EngageBLEServiceState.Idle)
    }

    @Test
    fun `it should handle BluetoothNotEnabled state correctly`() {
        // given
        val state = BLEServiceState.BluetoothNotEnabled
        start()

        // when
        bleServiceState.value = state

        // then
        assertState(state = EngageBLEServiceState.BluetoothNotEnabled)
    }

    @Test
    fun `it should handle MissingPermissions state correctly`() {
        // given
        val permissions = listOf("permission1", "permission2")
        val state = BLEServiceState.MissingPermissions(permissions)
        start()

        // when
        bleServiceState.value = state

        // then
        assertState(state = EngageBLEServiceState.MissingPermissions(permissions))
    }

    @Test
    fun `it should handle Scanning state correctly`() {
        // given
        val state = BLEServiceState.Scanning(emptyList())
        start()

        // when
        bleServiceState.value = state

        // then
        assertState(state = EngageBLEServiceState.Scanning(emptyList()))
    }

    @Test
    fun `it should handle GenericError correctly`() = runTestUnconfined {
        // given
        val event = BLEServiceEvent.GenericError(mockk())
        start()

        // when
        bleServiceEvents.emit(event)

        // then
        assertState(EngageBLEServiceState.Idle)
    }

    @Test
    fun `it should handle ScanningFailed correctly`() = runTestUnconfined {
        // given
        val event = BLEServiceEvent.ScanningFailed(1)
        start()

        // when
        bleServiceEvents.emit(event)

        // then
        assertState(EngageBLEServiceState.Idle)
    }

    @Test
    fun `it should handle connected event correctly`() = runTestUnconfined {
        // given
        val event = BLEServiceEvent.Connected(device)
        start()

        // when
        bleServiceEvents.emit(event)

        // then
        assertState(EngageBLEServiceState.Idle)
    }

    @Test
    fun `it should handle disconnected event correctly`() = runTestUnconfined {
        // given
        val event = BLEServiceEvent.Disconnected(device)
        start()

        // when
        bleServiceEvents.emit(event)

        // then
        assertState(EngageBLEServiceState.Idle)
    }

    @Test
    fun `it should handle CharacteristicChanged event correctly`() = runTestUnconfined {
        // given
        val characteristic = mockk<BluetoothGattCharacteristic>()
        val event = BLEServiceEvent.CharacteristicChanged(
            device = device,
            gatt = mockk(),
            characteristic = characteristic,
            value = byteArrayOf(),
        )
        val measurement: Measurement = mockk()
        coEvery { measurementMapper.map(characteristic, event.value) } returns measurement
        start()
        bleServiceState.value = BLEServiceState.Scanning(emptyList())

        // when
        bleServiceEvents.emit(event)

        // then
        assertState(
            state = EngageBLEServiceState.Scanning(
                sessions = listOf(BLEDeviceSession(device, listOf(measurement)))
            )
        )
        assertEvent(event = EngageBLEServiceEvent.MeasurementReceived(device, measurement))
    }

    @Suppress("MissingPermission", "DEPRECATION")
    @Test
    fun `it should handle ServiceDiscovered correctly`() = runTestUnconfined {
        val gatt: BluetoothGatt = mockk(relaxed = true)
        val device: BluetoothDevice = mockk {
            every { address } returns "address"
        }
        val gattService: BluetoothGattService = mockk()
        every { gatt.services } returns listOf(gattService)
        val characteristic: BluetoothGattCharacteristic = mockk()
        every { gattService.characteristics } returns listOf(characteristic)
        every { measurementMapper.recognises(characteristic) } returns true
        val descriptor: BluetoothGattDescriptor = mockk(relaxed = true)
        every { characteristic.getDescriptor(UUID("00002902-0000-1000-8000-00805f9b34fb")) } returns descriptor
        start()

        // when
        bleServiceEvents.emit(BLEServiceEvent.ServiceDiscovered(device, gatt, 1))

        // then
        verify { gatt.setCharacteristicNotification(characteristic, true) }
        verify { descriptor.value = BluetoothGattDescriptor.ENABLE_INDICATION_VALUE }
        verify { gatt.writeDescriptor(descriptor) }
    }

    @Test
    fun `it should handle stop correctly`() {
        // when
        service.stop()

        // then
        verify { bleService.stop() }
    }

    private fun assertState(state: EngageBLEServiceState) {
        assertThat(service.state.value).isEqualTo(state)
    }

    private suspend fun assertEvent(event: EngageBLEServiceEvent) {
        assertThat(service.events.first()).isEqualTo(event)
    }

    private fun start(autoConnect: Boolean = true) {
        service.start(autoConnect = autoConnect)
    }
}
