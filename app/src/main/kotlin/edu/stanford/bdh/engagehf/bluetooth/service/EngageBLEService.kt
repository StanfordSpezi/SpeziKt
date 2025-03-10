package edu.stanford.bdh.engagehf.bluetooth.service

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGattDescriptor
import edu.stanford.bdh.engagehf.bluetooth.service.mapper.MeasurementMapper
import edu.stanford.spezi.core.coroutines.Dispatching
import edu.stanford.spezi.core.logging.speziLogger
import edu.stanford.spezi.modules.bluetooth.api.BLEService
import edu.stanford.spezi.modules.bluetooth.data.model.BLEDevice
import edu.stanford.spezi.modules.bluetooth.data.model.BLEServiceEvent
import edu.stanford.spezi.modules.bluetooth.data.model.BLEServiceState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

class EngageBLEService @Inject constructor(
    private val bleService: BLEService,
    @Dispatching.IO private val ioScope: CoroutineScope,
    private val measurementMapper: MeasurementMapper,
) {
    private val logger by speziLogger()
    private val _events =
        MutableSharedFlow<EngageBLEServiceEvent>(replay = 1, extraBufferCapacity = 1)
    private var eventsJob: Job? = null
    private var stateJob: Job? = null

    private val _state = MutableStateFlow<EngageBLEServiceState>(EngageBLEServiceState.Idle)
    val state = _state.asStateFlow()
    val events: Flow<EngageBLEServiceEvent> = _events.asSharedFlow()

    fun start() {
        collectState()
        collectEvents()
        bleService.startDiscovering(services = BLEServiceType.entries.map { it.service })
    }

    private fun collectState() {
        stateJob?.cancel()
        stateJob = ioScope.launch {
            bleService.state.collect { state ->
                logger.i { "Received BLEService state $state" }
                when (state) {
                    is BLEServiceState.Idle -> _state.update { EngageBLEServiceState.Idle }
                    is BLEServiceState.BluetoothNotEnabled -> _state.update { EngageBLEServiceState.BluetoothNotEnabled }
                    is BLEServiceState.MissingPermissions -> _state.update {
                        EngageBLEServiceState.MissingPermissions(state.permissions)
                    }

                    is BLEServiceState.Scanning -> _state.update { currentState ->
                        val scanningState = currentState as? EngageBLEServiceState.Scanning
                        val sessions = state.devices.map { device ->
                            val measurements = scanningState
                                ?.sessions
                                ?.find { it.device.address == device.address }
                                ?.measurements ?: emptyList()
                            BLEDeviceSession(
                                device = device,
                                measurements = measurements,
                            )
                        }
                        EngageBLEServiceState.Scanning(sessions = sessions)
                    }
                }
            }
        }
    }

    private fun collectEvents() {
        if (eventsJob?.isActive == true) return
        eventsJob = ioScope.launch {
            bleService.events.collect { event ->
                logger.i { "Received BLEService event $event" }
                when (event) {
                    is BLEServiceEvent.GenericError,
                    is BLEServiceEvent.ScanningFailed,
                    is BLEServiceEvent.Disconnected,
                    is BLEServiceEvent.DeviceUnpaired,
                    -> {
                        logger.i { "Ignoring event $event" }
                    }

                    is BLEServiceEvent.DevicePaired -> {
                        _events.emit(EngageBLEServiceEvent.DevicePaired(event.device))
                    }
                    is BLEServiceEvent.Connected -> {
                        _events.emit(EngageBLEServiceEvent.DeviceConnected(event.device))
                    }
                    is BLEServiceEvent.DeviceDiscovered -> {
                        _events.emit(EngageBLEServiceEvent.DeviceDiscovered(event.device))
                    }

                    is BLEServiceEvent.CharacteristicChanged -> {
                        onCharacteristicChanged(event = event)
                    }

                    is BLEServiceEvent.ServiceDiscovered -> {
                        onServiceDiscovered(event = event)
                    }
                }
            }
        }
    }

    fun pair(bluetoothDevice: BluetoothDevice) {
        bleService.pair(device = bluetoothDevice)
    }

    fun stop() {
        eventsJob?.cancel()
        eventsJob = null
        stateJob?.cancel()
        stateJob = null
        bleService.stop()
    }

    @Suppress("MissingPermission", "DEPRECATION")
    private fun onServiceDiscovered(event: BLEServiceEvent.ServiceDiscovered) {
        event.gatt.services.forEach { service ->
            service.characteristics.forEach { characteristic ->
                if (measurementMapper.recognises(characteristic = characteristic)) {
                    val gatt = event.gatt
                    gatt.setCharacteristicNotification(characteristic, true)
                    val descriptor = characteristic.getDescriptor(BLEService.DESCRIPTOR_UUID)
                    descriptor.value = BluetoothGattDescriptor.ENABLE_INDICATION_VALUE
                    gatt.writeDescriptor(descriptor)
                }
            }
        }
    }

    private fun onCharacteristicChanged(event: BLEServiceEvent.CharacteristicChanged) {
        ioScope.launch {
            measurementMapper.map(characteristic = event.characteristic, data = event.value)
                ?.let { measurement ->
                    _state.update { state ->
                        val currentState =
                            state as? EngageBLEServiceState.Scanning ?: return@update state
                        val deviceAddress = event.device.address
                        val activeSessions = currentState.sessions
                        val otherDevicesSessions =
                            activeSessions.filterNot { it.device.address == deviceAddress }
                        val deviceSession =
                            activeSessions.find { it.device.address == deviceAddress }
                                ?.let { session ->
                                    if (session.measurements.contains(measurement)) {
                                        return@update currentState
                                    }
                                    session.copy(measurements = session.measurements + measurement)
                                } ?: BLEDeviceSession(
                                device = event.device,
                                measurements = listOf(measurement)
                            )
                        logger.i { "Updating $currentState with $measurement" }
                        _events.emit(
                            EngageBLEServiceEvent.MeasurementReceived(
                                device = event.device,
                                measurement = measurement
                            )
                        )
                        EngageBLEServiceState.Scanning(otherDevicesSessions + deviceSession)
                    }
                }
        }
    }
}

/**
 * Represents the state of [EngageBLEService]
 */
sealed interface EngageBLEServiceState {

    /**
     * Represents the idle initial state of [EngageBLEService]
     */
    data object Idle : EngageBLEServiceState

    /**
     * Represents the state indicating that Bluetooth is not enabled.
     */
    data object BluetoothNotEnabled : EngageBLEServiceState

    /**
     * Represents the state indicating missing permissions.
     *
     * @property permissions The list of permissions that are missing.
     */
    data class MissingPermissions(val permissions: List<String>) : EngageBLEServiceState

    /**
     * Represents the scanning state of the service.
     *
     * @property sessions The list of device sessions.
     */
    data class Scanning(val sessions: List<BLEDeviceSession>) : EngageBLEServiceState
}

/**
 * Represents an active ble session
 *
 * @property device Current paired device
 * @property measurements List of measurements received from the device
 */
data class BLEDeviceSession(
    val device: BLEDevice,
    val measurements: List<Measurement>,
)

/**
 * Represents the events emitted by [EngageBLEService]
 */
sealed interface EngageBLEServiceEvent {

    /**
     * Represents an event indicating that a new not yet paired device has been discovered
     * @property bluetoothDevice discovered device
     */
    data class DeviceDiscovered(
        val bluetoothDevice: BluetoothDevice,
    ) : EngageBLEServiceEvent

    /**
     * Represents an event indicating that a new device has been paired
     * @property bluetoothDevice paired device
     */
    data class DevicePaired(
        val bluetoothDevice: BluetoothDevice,
    ) : EngageBLEServiceEvent

    /**
     * Represents an event indicating that a new device has been connected
     * @property bleDevice paired device
     */
    data class DeviceConnected(
        val bleDevice: BLEDevice,
    ) : EngageBLEServiceEvent

    /**
     * Represents a measurement received event
     *
     * @property device BLE device which produced the measurement
     * @property measurement received measurement
     */
    data class MeasurementReceived(
        val device: BLEDevice,
        val measurement: Measurement,
    ) : EngageBLEServiceEvent
}
