package edu.stanford.spezi.core.bluetooth.domain

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import edu.stanford.spezi.core.bluetooth.data.model.BLEDevice
import edu.stanford.spezi.core.coroutines.di.Dispatching
import edu.stanford.spezi.core.logging.speziLogger
import edu.stanford.spezi.core.utils.TimeProvider
import edu.stanford.spezi.modules.storage.di.Storage
import edu.stanford.spezi.modules.storage.key.KeyValueStorage
import edu.stanford.spezi.modules.storage.key.getSerializableList
import edu.stanford.spezi.modules.storage.key.putSerializable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@Suppress("MissingPermission")
internal class BLEPairedDevicesStorage @Inject constructor(
    private val bluetoothAdapter: BluetoothAdapter,
    private val bleDevicePairingNotifier: BLEDevicePairingNotifier,
    @Storage.Encrypted
    private val storage: KeyValueStorage,
    private val timeProvider: TimeProvider,
    @Dispatching.IO private val ioScope: CoroutineScope,
) {
    private val logger by speziLogger()

    private val _pairedDevices = MutableStateFlow(emptyList<BLEDevice>())

    fun observePairedDevices(): StateFlow<List<BLEDevice>> {
        refreshState()
        observeUnpairingEvents()
        return _pairedDevices.asStateFlow()
    }

    fun updateDeviceConnection(device: BluetoothDevice, connected: Boolean) {
        if (isPaired(device).not()) return
        val currentDevices = getCurrentStoredDevices()
        currentDevices.removeAll { it.address == device.address }
        val newDevice = BLEDevice(
            address = device.address,
            name = device.name,
            connected = connected,
            lastSeenTimeStamp = timeProvider.currentTimeMillis()
        )

        update(devices = currentDevices + newDevice)
    }

    private fun refreshState() {
        val systemBoundDevices = bluetoothAdapter.bondedDevices ?: return
        val newDevices = getCurrentStoredDevices().filter { storedDevice ->
            systemBoundDevices.any { it.address == storedDevice.address }
        }
        logger.i { "refreshing with $newDevices" }
        update(devices = newDevices)
    }

    fun onStopped() {
        val devices = getCurrentStoredDevices().map {
            it.copy(connected = false, lastSeenTimeStamp = timeProvider.currentTimeMillis())
        }
        update(devices = devices)
    }

    private fun update(devices: List<BLEDevice>) {
        storage.putSerializable(key = KEY, devices)
        _pairedDevices.update { devices }
        logger.i { "Updating local storage with $devices" }
    }

    fun isPaired(bluetoothDevice: BluetoothDevice) = bluetoothAdapter.bondedDevices.any {
        it.address == bluetoothDevice.address
    }

    private fun observeUnpairingEvents() {
        ioScope.launch {
            bleDevicePairingNotifier
                .events
                .collect { event ->
                    when (event) {
                        is BLEDevicePairingNotifier.Event.DeviceUnpaired -> {
                            val devices = getCurrentStoredDevices()
                            devices.removeAll { storedDevice ->
                                storedDevice.address == event.device.address
                            }
                            update(devices)
                        }

                        is BLEDevicePairingNotifier.Event.DevicePaired -> {
                            val device = event.device
                            val currentDevices = getCurrentStoredDevices()
                            currentDevices.removeAll { it.address == device.address }
                            val newDevice = BLEDevice(
                                address = device.address,
                                name = device.name,
                                connected = true,
                                lastSeenTimeStamp = timeProvider.currentTimeMillis()
                            )

                            update(devices = currentDevices + newDevice)
                        }
                    }
                }
        }
    }

    private fun getCurrentStoredDevices() =
        storage.getSerializableList<BLEDevice>(key = KEY).toMutableList()

    private companion object {
        const val KEY = "paired_ble_devices"
    }
}
