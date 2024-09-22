package edu.stanford.spezi.core.bluetooth.domain

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import com.google.common.truth.Truth.assertThat
import edu.stanford.spezi.core.bluetooth.data.model.BLEDevice
import edu.stanford.spezi.core.testing.SpeziTestScope
import edu.stanford.spezi.core.testing.runTestUnconfined
import edu.stanford.spezi.modules.storage.key.InMemoryKeyValueStorage
import io.mockk.every
import io.mockk.mockk
import org.junit.Test
import kotlin.random.Random

class PairedDevicesStorageTest {
    private val address = "address"
    private val name = "name"
    private val storageKey = "paired_ble_devices"
    private val adapter: BluetoothAdapter = mockk()
    private val storage = InMemoryKeyValueStorage()
    private val bluetoothDevice: BluetoothDevice = mockk {
        every { name } returns this@PairedDevicesStorageTest.name
        every { address } returns this@PairedDevicesStorageTest.address
    }

    private val pairedDevicesStorage by lazy {
        PairedDevicesStorage(
            bluetoothAdapter = adapter,
            encryptedKeyValueStorage = storage,
            ioScope = SpeziTestScope(),
        )
    }

    @Test
    fun `it should update device correctly`() = runTestUnconfined {
        // when
        val connected = Random.nextBoolean()
        pairedDevicesStorage.updateDevice(bluetoothDevice, connected)
        val expectedDevice = BLEDevice(
            address = address,
            name = name,
            connected = connected,
        )

        // then
        assertThat(pairedDevicesStorage.pairedDevices.value).containsExactly(expectedDevice)
        assertThat(storage.getValue(storageKey)).isEqualTo(listOf(expectedDevice))
    }

    @Test
    fun `it should remove stored device on refresh if not bound anymore`() = runTestUnconfined {
        // when
        every { adapter.bondedDevices } returns mutableSetOf()
        val connected = Random.nextBoolean()
        val device = BLEDevice(
            address = address,
            name = name,
            connected = connected,
        )
        storage.putValue(storageKey, listOf(device))

        // when
        pairedDevicesStorage.refresh()

        // then
        assertThat(pairedDevicesStorage.pairedDevices.value).isEmpty()
        assertThat(storage.getValue(storageKey)).isEqualTo(emptyList<BLEDevice>())
    }

    @Test
    fun `it should handle on stopped correctly`() = runTestUnconfined {
        // when
        every { adapter.bondedDevices } returns mutableSetOf()
        val device = BLEDevice(
            address = address,
            name = name,
            connected = true,
        )
        val expectedDevice = device.copy(connected = false)
        storage.putValue(storageKey, listOf(device))

        // when
        pairedDevicesStorage.onStopped()

        // then
        assertThat(pairedDevicesStorage.pairedDevices.value).containsExactly(expectedDevice)
        assertThat(storage.getValue(storageKey)).isEqualTo(listOf(expectedDevice))
    }

    @Test
    fun `it should indicate is paired correctly`() {
        // given
        every { adapter.bondedDevices } returns mutableSetOf(bluetoothDevice)

        // when
        val result = pairedDevicesStorage.isPaired(bluetoothDevice)

        // then
        assertThat(result).isTrue()
    }
}
