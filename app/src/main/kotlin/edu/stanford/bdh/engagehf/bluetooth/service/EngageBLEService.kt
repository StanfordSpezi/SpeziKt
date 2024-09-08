package edu.stanford.bdh.engagehf.bluetooth.service

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGattDescriptor
import edu.stanford.bdh.engagehf.bluetooth.service.mapper.MeasurementMapper
import edu.stanford.spezi.core.bluetooth.api.BLEService
import edu.stanford.spezi.core.bluetooth.data.model.BLEServiceEvent
import edu.stanford.spezi.core.bluetooth.data.model.BLEServiceState
import edu.stanford.spezi.core.coroutines.di.Dispatching
import edu.stanford.spezi.core.logging.speziLogger
import edu.stanford.spezi.core.utils.extensions.append
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
        bleService.start(services = BLEServiceType.entries.map { it.service })
    }

    private fun collectState() {
        ioScope.launch {
            bleService.state.collect { state ->
                logger.i { "Received BLEService state $state" }
                when (state) {
                    is BLEServiceState.Idle -> _state.update { EngageBLEServiceState.Idle }
                    is BLEServiceState.BluetoothNotEnabled -> _state.update { EngageBLEServiceState.BluetoothNotEnabled }
                    is BLEServiceState.MissingPermissions -> _state.update {
                        EngageBLEServiceState.MissingPermissions(
                            state.permissions
                        )
                    }

                    is BLEServiceState.Scanning -> _state.update { currentState ->
                        if (currentState is EngageBLEServiceState.Scanning) {
                            currentState
                        } else {
                            val sessions = state.pairedDevices.map {
                                BLEDeviceSession(
                                    device = it,
                                    measurements = emptyList(),
                                )
                            }
                            EngageBLEServiceState.Scanning(sessions = sessions)
                        }
                    }
                }
            }
        }
    }

    private fun collectEvents() {
        ioScope.launch {
            bleService.events.collect { event ->
                logger.i { "Received BLEService event $event" }
                when (event) {
                    is BLEServiceEvent.GenericError,
                    is BLEServiceEvent.ScanningFailed,
                    -> {
                        logger.i { "Ignoring event $event" }
                    }

                    is BLEServiceEvent.Connected -> {
                        onDeviceConnected(event = event)
                    }

                    is BLEServiceEvent.Disconnected -> {
                        onDeviceDisconnected(event = event)
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
                        logger.i { "Updating $currentState with $measurement" }
                        val deviceAddress = event.device.address
                        val activeSessions = currentState.sessions
                        val otherDevicesSessions =
                            activeSessions.filterNot { it.device.address == deviceAddress }
                        val deviceSession =
                            activeSessions.find { it.device.address == deviceAddress }?.let { session ->
                                session.copy(measurements = session.measurements.append(measurement))
                            } ?: BLEDeviceSession(
                                device = event.device,
                                measurements = listOf(measurement)
                            )
                        EngageBLEServiceState.Scanning(otherDevicesSessions + deviceSession)
                    }
                    _events.emit(
                        EngageBLEServiceEvent.MeasurementReceived(
                            device = event.device,
                            measurement = measurement
                        )
                    )
                }
        }
    }

    private fun onDeviceConnected(event: BLEServiceEvent.Connected) {
        _state.update { state ->
            val scanning = state as? EngageBLEServiceState.Scanning ?: return@update state
            if (scanning.sessions.any { it.device.address == event.device.address }) return@update scanning
            val session = BLEDeviceSession(device = event.device, measurements = emptyList())
            scanning.copy(sessions = scanning.sessions + session)
        }
    }

    private fun onDeviceDisconnected(event: BLEServiceEvent.Disconnected) {
        _state.update { state ->
            val scanning = state as? EngageBLEServiceState.Scanning ?: return@update state
            val sessions = scanning.sessions.filter { it.device.address != event.device.address }
            scanning.copy(sessions = sessions)
        }
    }
}

sealed interface EngageBLEServiceState {

    /**
     * Represents the idle state of the BLE service.
     */
    data object Idle : EngageBLEServiceState

    /**
     * Represents an event indicating that Bluetooth is not enabled.
     */
    data object BluetoothNotEnabled : EngageBLEServiceState

    /**
     * Represents an event indicating missing permissions.
     *
     * @property permissions The list of permissions that are missing.
     */
    data class MissingPermissions(val permissions: List<String>) : EngageBLEServiceState

    /**
     * Represents the scanning state of the BLE service.
     *
     * @property sessions The list of active device sessions.
     */
    data class Scanning(val sessions: List<BLEDeviceSession>) : EngageBLEServiceState
}

data class BLEDeviceSession(
    val device: BluetoothDevice,
    val measurements: List<Measurement>,
)

sealed interface EngageBLEServiceEvent {
    data class MeasurementReceived(
        val device: BluetoothDevice,
        val measurement: Measurement,
    ) : EngageBLEServiceEvent
}
