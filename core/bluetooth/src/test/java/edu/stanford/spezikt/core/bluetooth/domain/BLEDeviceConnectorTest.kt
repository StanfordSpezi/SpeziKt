package edu.stanford.spezikt.core.bluetooth.domain

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothGattService
import android.bluetooth.BluetoothProfile
import android.content.Context
import com.google.common.truth.Truth.assertThat
import edu.stanford.spezi.core.testing.SpeziTestScope
import edu.stanford.spezi.core.testing.runTestUnconfined
import edu.stanford.spezi.core.testing.verifyNever
import edu.stanford.spezi.utils.UUID
import edu.stanford.spezikt.core.bluetooth.data.mapper.MeasurementMapper
import edu.stanford.spezikt.core.bluetooth.data.model.BLEServiceEvent
import edu.stanford.spezikt.core.bluetooth.data.model.Measurement
import io.mockk.Called
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import kotlinx.coroutines.flow.first
import org.junit.Before
import org.junit.Test

class BLEDeviceConnectorTest {
    private val device: BluetoothDevice = mockk()
    private val measurementMapper: MeasurementMapper = mockk()
    private val context: Context = mockk()
    private val bluetoothGatt: BluetoothGatt = mockk()

    private val bleDeviceConnector by lazy {
        BLEDeviceConnector(
            device = device,
            measurementMapper = measurementMapper,
            scope = SpeziTestScope(),
            context = context,
        )
    }

    @Before
    fun setup() {
        every { device.connectGatt(context, false, any()) } returns bluetoothGatt
        every { bluetoothGatt.disconnect() } just Runs
    }

    @Test
    fun `it should handle connect correctly`() {
        // given
        val sut = bleDeviceConnector

        // when
        repeat(10) { sut.connect() }

        // then
        verify(exactly = 1) { device.connectGatt(context, false, any()) }
    }

    @Test
    fun `it should handle disconnect correctly if not connected before`() = runTestUnconfined {
        // given
        val sut = bleDeviceConnector

        // when
        sut.disconnect()

        // then
        verify { bluetoothGatt wasNot Called }
        assertEvent(BLEServiceEvent.Disconnected(device))
    }

    @Test
    fun `it should handle disconnect correctly if connected before`() = runTestUnconfined {
        // given
        val sut = bleDeviceConnector
        sut.connect()

        // when
        sut.disconnect()

        // then
        verify { bluetoothGatt.disconnect() }
    }

    @Test
    fun `it should handle onConnectionStateChange state connected correctly`() = runTestUnconfined {
        // given
        val callback = getCallback()
        val gatt: BluetoothGatt = mockk(relaxed = true)

        // when
        callback.onConnectionStateChange(gatt, 1, BluetoothProfile.STATE_CONNECTED)

        // then
        verify { gatt.discoverServices() }
        assertEvent(BLEServiceEvent.Connected(device))
    }

    @Test
    fun `it should handle onConnectionStateChange state disconnected correctly`() = runTestUnconfined {
        // given
        val callback = getCallback()
        val gatt: BluetoothGatt = mockk(relaxed = true)

        // when
        callback.onConnectionStateChange(gatt, 1, BluetoothProfile.STATE_DISCONNECTED)

        // then
        assertEvent(BLEServiceEvent.Disconnected(device))
    }

    @Test
    fun `it should ignore explicit disconnects after receiving state disconnected`() = runTestUnconfined {
        // given
        val callback = getCallback()
        val gatt: BluetoothGatt = mockk(relaxed = true)
        callback.onConnectionStateChange(gatt, 1, BluetoothProfile.STATE_DISCONNECTED)

        // when
        bleDeviceConnector.disconnect()

        // then
        assertEvent(BLEServiceEvent.Disconnected(device))
        verify { bluetoothGatt wasNot Called }
    }

    @Test
    fun `it should handle onCharacteristicChanged state disconnected correctly`() = runTestUnconfined {
        // given
        val callback = getCallback()
        val gatt: BluetoothGatt = mockk(relaxed = true)
        val characteristic: BluetoothGattCharacteristic = mockk()
        val data = ByteArray(10)
        val measurement: Measurement = mockk()
        coEvery { measurementMapper.map(characteristic, data) } returns measurement

        // when
        callback.onCharacteristicChanged(gatt, characteristic, data)

        // then
        assertEvent(BLEServiceEvent.MeasurementReceived(device, measurement))
    }

    @Test
    fun `it should handle onServicesDiscovered state disconnected correctly`() = runTestUnconfined {
        // given
        val callback = getCallback()
        val gatt: BluetoothGatt = mockk(relaxed = true)
        val service: BluetoothGattService = mockk()
        every { gatt.services } returns listOf(service)
        val characteristic: BluetoothGattCharacteristic = mockk()
        every { service.characteristics } returns listOf(characteristic)
        every { measurementMapper.recognises(characteristic) } returns true
        val descriptor: BluetoothGattDescriptor = mockk(relaxed = true)
        every { characteristic.getDescriptor(UUID("00002902-0000-1000-8000-00805f9b34fb")) } returns descriptor

        // when
        callback.onServicesDiscovered(gatt, 1)

        // then
        verify { gatt.setCharacteristicNotification(characteristic, true) }
        verify { descriptor.value = BluetoothGattDescriptor.ENABLE_INDICATION_VALUE }
        verify { gatt.writeDescriptor(descriptor) }
    }

    @Test
    fun `it should handle onServicesDiscovered state disconnected correctly if not recognized`() = runTestUnconfined {
        // given
        val callback = getCallback()
        val gatt: BluetoothGatt = mockk(relaxed = true)
        val service: BluetoothGattService = mockk()
        every { gatt.services } returns listOf(service)
        val characteristic: BluetoothGattCharacteristic = mockk()
        every { service.characteristics } returns listOf(characteristic)
        every { measurementMapper.recognises(characteristic) } returns false

        // when
        callback.onServicesDiscovered(gatt, 1)

        // then
        verifyNever { gatt.setCharacteristicNotification(characteristic, true) }
        verifyNever { gatt.writeDescriptor(any()) }
    }

    private fun getCallback(): BluetoothGattCallback {
        val slot = slot<BluetoothGattCallback>()
        bleDeviceConnector.connect()
        verify(exactly = 1) { device.connectGatt(context, false, capture(slot)) }
        return slot.captured
    }

    private suspend fun assertEvent(event: BLEServiceEvent) {
        assertThat(bleDeviceConnector.events.first()).isEqualTo(event)
    }
}