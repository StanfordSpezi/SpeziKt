package edu.stanford.bdh.engagehf.bluetooth.pairing

import android.bluetooth.BluetoothDevice
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.stanford.bdh.engagehf.bluetooth.component.AppScreenEvents
import edu.stanford.bdh.engagehf.bluetooth.service.EngageBLEService
import edu.stanford.bdh.engagehf.bluetooth.service.EngageBLEServiceEvent
import edu.stanford.spezi.core.design.component.StringResource
import edu.stanford.spezi.core.logging.speziLogger
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
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

val discovering = BLEDevicePairingViewModel.UiState.Discovering(
    title = StringResource("Discovering"),
    subtitle = StringResource("Hold down the bluetooth button for 3 seconds to put the device into pairing mode."),
)

val found = BLEDevicePairingViewModel.UiState.DeviceFound(
    title = StringResource("Pair Accessory"),
    subtitle = StringResource("Do you want to pair DEVICE_NAME with ENGAGEHF app?"),
)

val success = BLEDevicePairingViewModel.UiState.DevicePaired(
    title = StringResource("Accessory Paired"),
    subtitle = StringResource("DEVICE_NAME was succesfully paired with ENGAGEHF app"),
)

@HiltViewModel
class BLEDevicePairingViewModel @Inject constructor(
    private val bleService: EngageBLEService,
    private val appScreenEvents: AppScreenEvents,
) : ViewModel() {
    private val logger by speziLogger()
    private var discoveringJob: Job? = null
    private var discoveredDevice: BluetoothDevice? = null
    private val _uiState = MutableStateFlow<UiState>(discovering)
    val uiState = _uiState.asStateFlow()

    init {
        start()
        observeAppScreenEvents()
    }

    private fun observeAppScreenEvents() {
        viewModelScope.launch {
            appScreenEvents.events.collect {
                logger.i { "Received app screen event $it" }
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
                    logger.i { "Received new collection" }
                    discoveredDevice = it.bluetoothDevice
                    _uiState.update { found }
                    discoveringJob?.cancel()
                    discoveringJob = null
                }
        }
    }

    private fun stop() {
        logger.i { "Stopping" }
        discoveringJob?.cancel()
        discoveringJob = null
        _uiState.update { discovering }
    }

    fun onAction(action: Action) {
        when (action) {
            is Action.Done -> {
                _uiState.update { discovering }
                appScreenEvents.emit(AppScreenEvents.Event.CloseBottomSheet)
            }

            is Action.Pair -> {
                pairDevice()
            }
        }
    }

    @OptIn(FlowPreview::class)
    private fun pairDevice() {
        val device = discoveredDevice ?: return

        var job: Job? = null

        job = viewModelScope.launch {
            delay(100L)
            bleService
                .events
                .filterIsInstance<EngageBLEServiceEvent.DevicePaired>()
                .filter { it.bluetoothDevice.address == device.address }
                .timeout(10.seconds)
                .catch { }
                .collect {
                    _uiState.update { success }
                    job?.cancel()
                    job = null
                }
        }
        bleService.pair(device)
    }

    sealed interface UiState {
        val title: StringResource
        val subtitle: StringResource
        val action: Action?

        data class Discovering(
            override val title: StringResource,
            override val subtitle: StringResource,
        ) : UiState {
            override val action = null
        }

        data class DeviceFound(
            override val title: StringResource,
            override val subtitle: StringResource,
        ) : UiState {
            override val action = Action.Pair
        }

        data class DevicePaired(
            override val title: StringResource,
            override val subtitle: StringResource,
        ) : UiState {
            override val action = Action.Done
        }
    }

    sealed interface Action {
        val actionTitle: StringResource
        data object Pair : Action {
            override val actionTitle: StringResource = StringResource("Pair")
        }
        data object Done : Action {
            override val actionTitle: StringResource = StringResource("Done")
        }
    }
}


