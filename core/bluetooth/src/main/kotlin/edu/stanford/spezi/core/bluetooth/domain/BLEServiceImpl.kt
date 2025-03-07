package edu.stanford.spezi.core.bluetooth.domain

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import edu.stanford.spezi.core.bluetooth.api.BLEService
import edu.stanford.spezi.core.bluetooth.data.model.BLEServiceEvent
import edu.stanford.spezi.core.bluetooth.data.model.BLEServiceState
import edu.stanford.spezi.core.utils.PermissionChecker
import edu.stanford.spezi.spezi.core.logging.coroutines.di.Dispatching
import edu.stanford.spezi.spezi.core.logging.speziLogger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Singleton class representing a BLE (Bluetooth Low Energy) service.
 *
 * This service manages the BLE functionality, including device scanning and connection.
 *
 * @property bluetoothAdapter The Bluetooth adapter used for BLE operations.
 * @property permissionChecker The permission checker used to check BLE-related permissions.
 * @property deviceScanner The BLE device scanner used for scanning nearby devices.
 * @property scope The coroutine scope used for launching BLE-related operations.
 * @property deviceConnectorFactory The factory used for creating instances of [BLEDeviceConnector].
 */
@Suppress("LongParameterList")
@Singleton
internal class BLEServiceImpl @Inject constructor(
    private val bluetoothAdapter: BluetoothAdapter,
    private val permissionChecker: PermissionChecker,
    private val deviceScanner: BLEDeviceScanner,
    private val pairedDevicesStorage: BLEPairedDevicesStorage,
    private val bleDevicePairingNotifier: BLEDevicePairingNotifier,
    @Dispatching.IO private val scope: CoroutineScope,
    private val deviceConnectorFactory: BLEDeviceConnector.Factory,
) : BLEService {

    private val logger by speziLogger()

    private val connectedDevices = ConcurrentHashMap<String, BLEDeviceConnector>()
    private val _state = MutableStateFlow<BLEServiceState>(BLEServiceState.Idle)
    private val _events = MutableSharedFlow<BLEServiceEvent>(replay = 1, extraBufferCapacity = 1)

    override val state: StateFlow<BLEServiceState> = _state.asStateFlow()
    override val events: Flow<BLEServiceEvent> = _events.asSharedFlow()

    override fun startDiscovering(services: List<UUID>) {
        logger.i { "start($services) requested" }
        when {
            bluetoothAdapter.isEnabled.not() -> {
                logger.i { "Start ignored, bluetooth not enabled" }
                deviceScanner.stopScanning()
                _state.update { BLEServiceState.BluetoothNotEnabled }
            }

            deviceScanner.isScanning -> logger.i { "Already scanning. Ignoring start request" }

            else -> {
                val missingPermissions =
                    REQUIRED_PERMISSIONS.filterNot { permissionChecker.isPermissionGranted(it) }
                if (missingPermissions.isNotEmpty()) {
                    _state.update { BLEServiceState.MissingPermissions(permissions = missingPermissions) }
                } else {
                    startPairingDevicesCollection()
                    startPairedDevicesStorageCollection()
                    startScannerEventCollection()
                    deviceScanner.startScanning(services = services)
                }
            }
        }
    }

    override fun pair(device: BluetoothDevice) {
        val deviceConnector = deviceConnectorFactory.create(device)
        deviceConnector.connect()
        scope.launch {
            deviceConnector.events
                .onEach {
                    logger.i { "Received device connector event $it for device ${device.address}" }
                    _events.emit(it)
                }
                .collect { event ->
                    when (event) {
                        is BLEServiceEvent.Connected -> updateState(
                            device = device,
                            deviceConnector = deviceConnector,
                        )

                        is BLEServiceEvent.Disconnected -> updateState(
                            device = device,
                            deviceConnector = null,
                        )

                        else -> Unit
                    }
                }
        }
    }

    override fun stop() {
        logger.i { "stop()" }
        deviceScanner.stopScanning()
        connectedDevices.keys.forEach { address ->
            val connector = connectedDevices[address]
            connector?.disconnect()
            connectedDevices.remove(address)
        }
        bleDevicePairingNotifier.stop()
        pairedDevicesStorage.onStopped()
    }

    private fun startScannerEventCollection() {
        scope.launch {
            deviceScanner.events
                .onEach { logger.i { "Received scanner event $it" } }
                .collect { event ->
                    when (event) {
                        is BLEDeviceScanner.Event.DeviceFound -> onDeviceFound(
                            device = event.device,
                        )

                        is BLEDeviceScanner.Event.Failure -> _events.emit(
                            BLEServiceEvent.ScanningFailed(
                                errorCode = event.errorCode
                            )
                        )
                    }
                }
        }
    }

    private fun startPairedDevicesStorageCollection() {
        scope.launch {
            pairedDevicesStorage.observePairedDevices().collect { devices ->
                _state.update { BLEServiceState.Scanning(devices) }
            }
        }
    }

    private fun startPairingDevicesCollection() {
        bleDevicePairingNotifier.start()
        scope.launch {
            bleDevicePairingNotifier.events.collect {
                logger.i { "Received pairing event $it" }
                when (it) {
                    is BLEDevicePairingNotifier.Event.DevicePaired -> {
                        _events.emit(BLEServiceEvent.DevicePaired(it.device))
                    }

                    is BLEDevicePairingNotifier.Event.DeviceUnpaired -> {
                        _events.emit(BLEServiceEvent.DeviceUnpaired(it.device))
                    }
                }
            }
        }
    }

    private suspend fun onDeviceFound(
        device: BluetoothDevice,
    ) {
        if (connectedDevices[device.address] != null) {
            return logger.i { "Device ${device.address} already known. Ignoring device found event" }
        }
        if (pairedDevicesStorage.isPaired(device)) {
            pair(device = device)
        } else {
            _events.emit(BLEServiceEvent.DeviceDiscovered(device))
        }
    }

    private fun updateState(
        device: BluetoothDevice,
        deviceConnector: BLEDeviceConnector?,
    ) {
        val deviceAddress = device.address
        val isConnected = deviceConnector != null
        deviceConnector?.let { connector ->
            if (connectedDevices.containsKey(deviceAddress).not()) {
                connectedDevices[deviceAddress] = connector
            }
        } ?: connectedDevices.remove(deviceAddress)
        pairedDevicesStorage.updateDeviceConnection(device = device, connected = isConnected)
    }

    private companion object {
        val REQUIRED_PERMISSIONS = listOf(
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
        )
    }
}
