package edu.stanford.spezi.core.bluetooth.domain

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import com.google.common.truth.Truth.assertThat
import edu.stanford.spezi.core.bluetooth.data.model.BLEDevice
import edu.stanford.spezi.core.testing.SpeziTestScope
import edu.stanford.spezi.core.testing.runTestUnconfined
import edu.stanford.spezi.core.utils.TimeProvider
import edu.stanford.spezi.spezi.credentialstorage.key.InMemoryKeyValueStorage
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.MutableSharedFlow
import org.junit.Before
import org.junit.Test
import kotlin.random.Random

class BLEPairedDevicesStorageTest {
    private val address = "address"
    private val name = "name"
    private val storageKey = "paired_ble_devices"
    private val adapter: BluetoothAdapter = mockk()
    private val storage = InMemoryKeyValueStorage()
    private val bluetoothDevice: BluetoothDevice = mockk {
        every { name } returns this@BLEPairedDevicesStorageTest.name
        every { address } returns this@BLEPairedDevicesStorageTest.address
    }
    private val timeProvider: TimeProvider = mockk()
    private val notifierEvents = MutableSharedFlow<BLEDevicePairingNotifier.Event>()
    private val bleDevicePairingNotifier: BLEDevicePairingNotifier = mockk(relaxed = true)

    private val pairedDevicesStorage by lazy {
        BLEPairedDevicesStorage(
            bluetoothAdapter = adapter,
            storage = storage,
            ioScope = SpeziTestScope(),
            bleDevicePairingNotifier = bleDevicePairingNotifier,
            timeProvider = timeProvider,
        )
    }

    @Before
    fun setup() = runTestUnconfined {
        storage.clear()
        every { bleDevicePairingNotifier.events } returns notifierEvents
        every { adapter.bondedDevices } returns mutableSetOf(bluetoothDevice)
        every { timeProvider.currentTimeMillis() } returns 0L
    }

    @Test
    fun `it should update device correctly`() = runTestUnconfined {
        // when
        val connected = Random.nextBoolean()
        pairedDevicesStorage.updateDeviceConnection(bluetoothDevice, connected)
        val expectedDevice = BLEDevice(
            address = address,
            name = name,
            connected = connected,
        )

        // then
        assertThat(pairedDevicesStorage.observePairedDevices().value).containsExactly(expectedDevice)
        assertThat(storage.getValue(storageKey)).isEqualTo(listOf(expectedDevice))
    }

    @Test
    fun `it should ignore update device if not paired correctly`() = runTestUnconfined {
        // when
        every { adapter.bondedDevices } returns mutableSetOf()
        pairedDevicesStorage.updateDeviceConnection(bluetoothDevice, false)

        // then
        assertThat(pairedDevicesStorage.observePairedDevices().value).isEmpty()
        assertThat(storage.getValue(storageKey)).isEqualTo(listOf<BLEDevice>())
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

        // then
        assertThat(pairedDevicesStorage.observePairedDevices().value).isEmpty()
        assertThat(storage.getValue(storageKey)).isEqualTo(emptyList<BLEDevice>())
    }

    @Test
    fun `it should handle on stopped correctly`() = runTestUnconfined {
        // given
        val device = BLEDevice(
            address = address,
            name = name,
            connected = true,
        )
        val disconnectedDevice = device.copy(connected = false)
        val expectedList = listOf(disconnectedDevice)
        storage.putValue(storageKey, listOf(device))

        // when
        pairedDevicesStorage.onStopped()

        // then
        assertThat(pairedDevicesStorage.observePairedDevices().value).isEqualTo(expectedList)
        assertThat(storage.getValue(storageKey)).isEqualTo(expectedList)
    }

    @Test
    fun `it should indicate is paired correctly`() {
        // given
        val contains = Random.nextBoolean()
        every { adapter.bondedDevices } answers {
            if (contains) mutableSetOf(bluetoothDevice) else emptySet()
        }

        // when
        val result = pairedDevicesStorage.isPaired(bluetoothDevice)

        // then
        assertThat(result).isEqualTo(contains)
    }

    @Test
    fun `it should start observing notifier events when paired devices are requested`() =
        runTestUnconfined {
            // when
            pairedDevicesStorage.observePairedDevices()

            // then
            verify { bleDevicePairingNotifier.events }
        }

    @Test
    fun `it should add device on paired event`() = runTestUnconfined {
        // given
        pairedDevicesStorage.observePairedDevices()
        val expectedList = listOf(
            BLEDevice(
                address = address,
                name = name,
                connected = true,
            )
        )

        // when
        notifierEvents.emit(BLEDevicePairingNotifier.Event.DevicePaired(bluetoothDevice))

        // then
        assertThat(pairedDevicesStorage.observePairedDevices().value).isEqualTo(expectedList)
        assertThat(storage.getValue(storageKey)).isEqualTo(expectedList)
    }

    @Test
    fun `it should remove device on unpaired event`() = runTestUnconfined {
        // given
        pairedDevicesStorage.observePairedDevices()
        val device = BLEDevice(
            address = address,
            name = name,
            connected = true,
        )
        storage.putValue(storageKey, listOf(device))

        // when
        notifierEvents.emit(BLEDevicePairingNotifier.Event.DeviceUnpaired(bluetoothDevice))

        // then
        assertThat(pairedDevicesStorage.observePairedDevices().value).isEmpty()
        assertThat(storage.getValue(storageKey)).isEqualTo(emptyList<BLEDevice>())
    }
}
