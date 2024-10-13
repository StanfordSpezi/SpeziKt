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
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@Suppress("MissingPermission")
internal class BLEPairedDevicesStorage @Inject constructor(
    private val bluetoothAdapter: BluetoothAdapter,
    private val bleDevicePairingNotifier: BLEDevicePairingNotifier,
    @Storage.Encrypted
    private val encryptedKeyValueStorage: KeyValueStorage,
    private val timeProvider: TimeProvider,
    @Dispatching.IO private val ioScope: CoroutineScope,
) {
    private val logger by speziLogger()
    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, error ->
        logger.e(error) { "Error executing paired devices storage operations" }
    }

    private val _pairedDevices = MutableStateFlow(emptyList<BLEDevice>())
    val pairedDevices = _pairedDevices.asStateFlow()

    init {
        refreshState()
        observeUnpairingEvents()
    }

    fun updateDeviceConnection(device: BluetoothDevice, connected: Boolean) = execute {
        if (isPaired(device).not()) return@execute
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

    private fun refreshState() = execute {
        val systemBoundDevices = bluetoothAdapter.bondedDevices ?: return@execute
        val newDevices = getCurrentStoredDevices().filter { storedDevice ->
            systemBoundDevices.any { it.address == storedDevice.address }
        }
        logger.i { "refreshing with $newDevices" }
        update(devices = newDevices)
    }

    fun onStopped() = execute {
        val devices = getCurrentStoredDevices().map {
            it.copy(connected = false, lastSeenTimeStamp = timeProvider.currentTimeMillis())
        }
        update(devices = devices)
    }

    private fun update(devices: List<BLEDevice>) = execute {
        encryptedKeyValueStorage.putSerializable(key = KEY, devices)
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

    private suspend fun getCurrentStoredDevices() =
        encryptedKeyValueStorage.getSerializableList<BLEDevice>(key = KEY).toMutableList()

    private fun execute(block: suspend () -> Unit) {
        ioScope.launch(coroutineExceptionHandler) { block() }
    }

    private companion object {
        const val KEY = "paired_ble_devices"
    }
}
