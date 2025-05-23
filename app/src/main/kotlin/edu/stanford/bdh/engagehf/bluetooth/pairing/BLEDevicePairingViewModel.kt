package edu.stanford.bdh.engagehf.bluetooth.pairing

import android.bluetooth.BluetoothDevice
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.stanford.bdh.engagehf.R
import edu.stanford.bdh.engagehf.bluetooth.component.AppScreenEvents
import edu.stanford.bdh.engagehf.bluetooth.service.EngageBLEService
import edu.stanford.bdh.engagehf.bluetooth.service.EngageBLEServiceEvent
import edu.stanford.spezi.ui.PendingActions
import edu.stanford.spezi.ui.StringResource
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.timeout
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class BLEDevicePairingViewModel @Inject constructor(
    private val bleService: EngageBLEService,
    private val uiStateMapper: BLEDevicePairingUiStateMapper,
    private val appScreenEvents: AppScreenEvents,
) : ViewModel() {
    private var discoveringJob: Job? = null
    private var discoveredDevice: BluetoothDevice? = null
    private val _uiState = MutableStateFlow<UiState>(uiStateMapper.mapInitialState())
    val uiState = _uiState.asStateFlow()

    init {
        start()
        observeAppScreenEvents()
    }

    fun onAction(action: Action) {
        when (action) {
            is Action.Done -> {
                appScreenEvents.emit(AppScreenEvents.Event.CloseBottomSheet)
            }

            is Action.Pair -> {
                pairDevice()
            }
        }
    }

    private fun observeAppScreenEvents() {
        viewModelScope.launch {
            appScreenEvents.events.collect {
                when (it) {
                    AppScreenEvents.Event.CloseBottomSheet -> stop()
                    AppScreenEvents.Event.BLEDevicePairingBottomSheet -> start()
                    else -> {}
                }
            }
        }
    }

    private fun start() {
        if (discoveringJob?.isActive == true) return
        discoveringJob = viewModelScope.launch {
            bleService.start()
            bleService.events
                .filterIsInstance<EngageBLEServiceEvent.DeviceDiscovered>()
                .take(1)
                .collect {
                    discoveredDevice = it.bluetoothDevice
                    _uiState.update { _ -> uiStateMapper.mapDeviceFoundState(it.bluetoothDevice) }
                    discoveringJob?.cancel()
                    discoveringJob = null
                }
        }
    }

    private fun stop() {
        discoveringJob?.cancel()
        discoveringJob = null
        _uiState.update { uiStateMapper.mapInitialState() }
    }

    @OptIn(FlowPreview::class)
    private fun pairDevice() {
        val device = discoveredDevice ?: return
        var job: Job? = null
        job = viewModelScope.launch {
            bleService.pair(device)
            _uiState.update {
                if (it is UiState.DeviceFound) {
                    it.copy(pendingActions = it.pendingActions + Action.Pair)
                } else {
                    it
                }
            }
            bleService
                .events
                .filterIsInstance<EngageBLEServiceEvent.DevicePaired>()
                .filter { it.bluetoothDevice.address == device.address }
                .timeout(20.seconds)
                .catch { _uiState.update { uiStateMapper.mapErrorState() } }
                .collect {
                    _uiState.update { uiStateMapper.mapDevicePairedState(device) }
                    job?.cancel()
                    job = null
                }
        }
    }

    sealed interface UiState {
        val title: StringResource
        val subtitle: StringResource
        val action: Action?

        data class Discovering(
            override val title: StringResource,
            override val subtitle: StringResource,
        ) : UiState {
            override val action: Action? = null
        }

        data class DeviceFound(
            override val title: StringResource,
            override val subtitle: StringResource,
            val pendingActions: PendingActions<Action.Pair> = PendingActions(),
        ) : UiState {
            override val action = Action.Pair
        }

        data class DevicePaired(
            override val title: StringResource,
            override val subtitle: StringResource,
        ) : UiState {
            override val action = Action.Done
        }

        data class Error(
            override val title: StringResource,
            override val subtitle: StringResource,
        ) : UiState {
            override val action = Action.Done
        }
    }

    sealed interface Action {
        val title: StringResource

        data object Pair : Action {
            override val title = StringResource(R.string.ble_device_pair_action_title)
        }

        data object Done : Action {
            override val title = StringResource(R.string.ble_device_pair_done_action_title)
        }
    }
}
