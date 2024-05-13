package edu.stanford.spezikt.core.bluetooth.domain

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import edu.stanford.spezi.logging.speziLogger
import edu.stanford.spezi.utils.extensions.append
import edu.stanford.spezikt.core.bluetooth.api.BLEService
import edu.stanford.spezikt.core.bluetooth.data.model.BLEDeviceSession
import edu.stanford.spezikt.core.bluetooth.data.model.BLEServiceEvent
import edu.stanford.spezikt.core.bluetooth.data.model.BLEServiceState
import edu.stanford.spezikt.core.bluetooth.data.model.Measurement
import edu.stanford.spezikt.coroutines.di.Dispatching
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
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class BLEServiceImpl @Inject constructor(
    private val bluetoothAdapter: BluetoothAdapter,
    private val permissionHandler: PermissionHandler,
    private val deviceScanner: BLEDeviceScanner,
    @Dispatching.IO private val scope: CoroutineScope,
    private val deviceConnectorFactory: BLEDeviceConnector.Factory,
) : BLEService {

    private val logger by speziLogger()

    private val connectedDevices = ConcurrentHashMap<String, BLEDeviceConnector>()
    private val _state = MutableStateFlow<BLEServiceState>(BLEServiceState.Idle)
    private val _events = MutableSharedFlow<BLEServiceEvent>(replay = 1, extraBufferCapacity = 1)

    override val state: StateFlow<BLEServiceState> = _state.asStateFlow()
    override val events: Flow<BLEServiceEvent> = _events.asSharedFlow()

    override fun start() {
        logger.i { "start() requested" }
        when {
            deviceScanner.isScanning -> logger.i { "Already scanning. Ignoring start request" }
            bluetoothAdapter.isEnabled.not() -> logger.i { "Start ignored, bluetooth not enabled" }
            else -> {
                val missingPermissions = REQUIRED_PERMISSIONS.filterNot { permissionHandler.isPermissionGranted(it) }
                if (missingPermissions.isNotEmpty()) {
                    emitEvent(event = BLEServiceEvent.MissingPermissions(permissions = missingPermissions))
                } else {
                    startScannerEventCollection()
                    emitEvent(event = BLEServiceEvent.ScanningStarted)
                    deviceScanner.startScanning()
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
        _state.update { BLEServiceState.Idle }
    }

    private fun startScannerEventCollection() {
        scope.launch {
            deviceScanner.events
                .onEach { logger.i { "Received scanner event $it" } }
                .collect { event ->
                    when (event) {
                        is BLEDeviceScanner.Event.DeviceFound -> onDeviceFound(event.device)
                        is BLEDeviceScanner.Event.Failure -> _events.emit(BLEServiceEvent.ScanningFailed(errorCode = event.errorCode))
                    }
                }
        }
    }

    private fun onDeviceFound(device: BluetoothDevice) {
        if (connectedDevices[device.address] != null) {
            return logger.i { "Device ${device.address} already known. Ignoring device found event" }
        }
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
                            measurement = null,
                        )
                        is BLEServiceEvent.Disconnected -> updateState(
                            device = device,
                            deviceConnector = null,
                            measurement = null
                        )
                        is BLEServiceEvent.MeasurementReceived -> updateState(
                            device = device,
                            deviceConnector = deviceConnector,
                            measurement = event.measurement
                        )
                        else -> Unit
                    }
                }
        }
    }

    private fun emitEvent(event: BLEServiceEvent) {
        scope.launch { _events.emit(event) }
    }

    private fun updateState(
        device: BluetoothDevice,
        deviceConnector: BLEDeviceConnector?,
        measurement: Measurement?,
    ) {
        val isConnected = deviceConnector != null
        val deviceAddress = device.address
        deviceConnector?.let { connector ->
            if (connectedDevices.containsKey(deviceAddress).not()) connectedDevices[deviceAddress] = connector
        } ?: connectedDevices.remove(deviceAddress)

        _state.update { currentState ->
            when (currentState) {
                is BLEServiceState.Idle -> {
                    if (isConnected) {
                        val deviceSession = BLEDeviceSession(device = device, measurements = listOfNotNull(measurement))
                        BLEServiceState.Scanning(sessions = listOf(deviceSession))
                    } else {
                        currentState
                    }
                }
                is BLEServiceState.Scanning -> {
                    val activeSessions = currentState.sessions
                    val otherDevicesSessions = activeSessions.filterNot { it.device.address == deviceAddress }
                    if (isConnected) {
                        val deviceSession = activeSessions.find { it.device.address == deviceAddress }?.let { session ->
                            session.copy(measurements = session.measurements.append(measurement))
                        } ?: BLEDeviceSession(device = device, measurements = listOfNotNull(measurement))

                        BLEServiceState.Scanning(sessions = otherDevicesSessions.append(deviceSession))
                    } else {
                        BLEServiceState.Scanning(sessions = otherDevicesSessions)
                    }
                }
            }.also {
                logger.i { "Updating own state due to $deviceAddress to $it" }
            }
        }
    }

    private companion object {
        val REQUIRED_PERMISSIONS = listOf(
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.BLUETOOTH_SCAN,
        )
    }
}