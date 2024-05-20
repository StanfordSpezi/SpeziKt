package edu.stanford.spezikt.bluetooth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.stanford.spezi.logging.speziLogger
import edu.stanford.spezikt.bluetooth.data.mapper.BluetoothUiStateMapper
import edu.stanford.spezikt.bluetooth.data.models.BluetoothUiState
import edu.stanford.spezikt.core.bluetooth.api.BLEService
import edu.stanford.spezikt.core.bluetooth.data.model.BLEServiceEvent
import edu.stanford.spezikt.core.bluetooth.data.model.BLEServiceState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BluetoothViewModel @Inject internal constructor(
    private val bleService: BLEService,
    private val uiStateMapper: BluetoothUiStateMapper,
) : ViewModel() {
    private val logger by speziLogger()

    private val _events = MutableSharedFlow<Event>(replay = 1, extraBufferCapacity = 1)
    private val _uiState = MutableStateFlow<BluetoothUiState>(BluetoothUiState.Idle)

    val uiState = _uiState.asStateFlow()
    val events = _events.asSharedFlow()

    init {
        start()
    }

    private fun start() {
        bleService.start()
        viewModelScope.launch {
            bleService.state.collect { state ->
                logger.i { "Received BLEService state $state" }
                when (state) {
                    BLEServiceState.Idle -> _uiState.update { BluetoothUiState.Idle }
                    is BLEServiceState.Scanning -> _uiState.update { uiStateMapper.map(state) }
                }
            }
        }

        viewModelScope.launch {
            bleService.events.collect { event ->
                logger.i { "Received BLEService event $event" }
                when (event) {
                    BLEServiceEvent.BluetoothNotEnabled -> _events.emit(Event.EnableBluetooth)
                    is BLEServiceEvent.MissingPermissions -> _events.emit(Event.RequestPermissions(event.permissions))
                    is BLEServiceEvent.GenericError -> _uiState.update { BluetoothUiState.Error("Something went wrong!") }
                    is BLEServiceEvent.ScanningFailed -> _uiState.update { BluetoothUiState.Error("Error while scanning for devices") }
                    BLEServiceEvent.ScanningStarted -> _uiState.update { BluetoothUiState.Scanning }
                    is BLEServiceEvent.Connected, is BLEServiceEvent.Disconnected, is BLEServiceEvent.MeasurementReceived -> {
                        logger.i { "Ignoring $event as it will be handled via BLEService state" }
                    }
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        bleService.stop()
    }

    interface Event {
        object EnableBluetooth : Event
        data class RequestPermissions(val permissions: List<String>) : Event
    }
}