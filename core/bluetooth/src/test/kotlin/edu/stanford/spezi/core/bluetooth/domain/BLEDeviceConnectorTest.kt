package edu.stanford.spezi.core.bluetooth.domain

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothProfile
import android.content.Context
import com.google.common.truth.Truth.assertThat
import edu.stanford.spezi.core.bluetooth.data.model.BLEDevice
import edu.stanford.spezi.core.bluetooth.data.model.BLEServiceEvent
import edu.stanford.spezi.core.testing.SpeziTestScope
import edu.stanford.spezi.core.testing.runTestUnconfined
import io.mockk.Called
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import kotlinx.coroutines.flow.first
import org.junit.Before
import org.junit.Test

class BLEDeviceConnectorTest {
    private val bleDevice = BLEDevice(
        name = "device",
        address = "address",
        connected = false,
    )
    private val device: BluetoothDevice = mockk {
        every { name } returns bleDevice.name
        every { address } returns bleDevice.address
    }

    private val context: Context = mockk()
    private val bluetoothGatt: BluetoothGatt = mockk()

    private val bleDeviceConnector by lazy {
        BLEDeviceConnector(
            device = device,
            scope = SpeziTestScope(),
            context = context,
        )
    }

    @Before
    fun setup() {
        every { device.createBond() } returns true
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
        verify(exactly = 1) { device.createBond() }
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
        assertEvent(BLEServiceEvent.Disconnected(bleDevice.copy(connected = false)))
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
        assertEvent(BLEServiceEvent.Connected(bleDevice.copy(connected = true)))
    }

    @Test
    fun `it should handle onConnectionStateChange state disconnected correctly`() = runTestUnconfined {
        // given
        val callback = getCallback()
        val gatt: BluetoothGatt = mockk(relaxed = true)

        // when
        callback.onConnectionStateChange(gatt, 1, BluetoothProfile.STATE_DISCONNECTED)

        // then
        assertEvent(BLEServiceEvent.Disconnected(bleDevice.copy(connected = false)))
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
        assertEvent(BLEServiceEvent.Disconnected(bleDevice.copy(connected = false)))
        verify { bluetoothGatt wasNot Called }
    }

    @Test
    fun `it should handle onCharacteristicChanged state disconnected correctly`() = runTestUnconfined {
        // given
        val callback = getCallback()
        val gatt: BluetoothGatt = mockk(relaxed = true)
        val characteristic: BluetoothGattCharacteristic = mockk()
        val data = ByteArray(10)

        // when
        callback.onCharacteristicChanged(gatt, characteristic, data)

        // then
        assertEvent(BLEServiceEvent.CharacteristicChanged(bleDevice.copy(connected = true), gatt, characteristic, value = data))
    }

    @Test
    fun `it should handle onServicesDiscovered state disconnected correctly`() = runTestUnconfined {
        // given
        val callback = getCallback()
        val gatt: BluetoothGatt = mockk(relaxed = true)
        val status = 42

        // when
        callback.onServicesDiscovered(gatt, status)

        // then
        assertEvent(event = BLEServiceEvent.ServiceDiscovered(device = device, gatt = gatt, status = status))
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
