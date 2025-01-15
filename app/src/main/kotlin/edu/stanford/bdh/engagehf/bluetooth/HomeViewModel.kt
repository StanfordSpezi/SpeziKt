package edu.stanford.bdh.engagehf.bluetooth

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import edu.stanford.bdh.engagehf.bluetooth.component.AppScreenEvents
import edu.stanford.bdh.engagehf.bluetooth.data.mapper.BluetoothUiStateMapper
import edu.stanford.bdh.engagehf.bluetooth.data.models.Action
import edu.stanford.bdh.engagehf.bluetooth.data.models.UiState
import edu.stanford.bdh.engagehf.bluetooth.measurements.MeasurementsRepository
import edu.stanford.bdh.engagehf.bluetooth.service.EngageBLEService
import edu.stanford.bdh.engagehf.bluetooth.service.EngageBLEServiceEvent
import edu.stanford.bdh.engagehf.bluetooth.service.EngageBLEServiceState
import edu.stanford.bdh.engagehf.messages.Message
import edu.stanford.bdh.engagehf.messages.MessagesHandler
import edu.stanford.bdh.engagehf.navigation.screens.BottomBarItem
import edu.stanford.spezi.core.logging.speziLogger
import edu.stanford.spezi.core.notification.NotificationPermissions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
@Suppress("LongParameterList")
class HomeViewModel @Inject internal constructor(
    private val bleService: EngageBLEService,
    private val uiStateMapper: BluetoothUiStateMapper,
    private val measurementsRepository: MeasurementsRepository,
    private val appScreenEvents: AppScreenEvents,
    private val messagesHandler: MessagesHandler,
    @ApplicationContext private val context: Context,
    notificationPermissions: NotificationPermissions,
) : ViewModel() {
    private val logger by speziLogger()

    private val _uiState = MutableStateFlow(
        UiState(
            missingPermissions = notificationPermissions.getRequiredPermissions()
        )
    )

    val uiState = _uiState.asStateFlow()

    init {
        bleService.start()
        observeBleService()
        observeRecords()
        observeMessages()
    }

    private fun observeBleService() {
        viewModelScope.launch {
            bleService.state.collect { state ->
                logger.i { "Received EngageBLEService state $state" }
                _uiState.update { currentState ->
                    val missingPermissions = currentState.missingPermissions.toMutableSet()
                    if (state is EngageBLEServiceState.MissingPermissions) {
                        missingPermissions.addAll(state.permissions)
                    }
                    currentState.copy(
                        bluetooth = uiStateMapper.mapBleServiceState(state),
                        missingPermissions = missingPermissions,
                    )
                }
            }
        }

        viewModelScope.launch {
            bleService.events.collect { event ->
                logger.i { "Received BLEService event $event" }
                when (event) {
                    is EngageBLEServiceEvent.MeasurementReceived -> {
                        appScreenEvents.emit(AppScreenEvents.Event.CloseBottomSheet)
                        _uiState.update {
                            val dialog = uiStateMapper.mapMeasurementDialog(event.measurement)
                            it.copy(measurementDialog = dialog)
                        }
                    }

                    is EngageBLEServiceEvent.DeviceDiscovered,
                    is EngageBLEServiceEvent.DeviceConnected,
                    is EngageBLEServiceEvent.DevicePaired,
                    -> {
                        logger.i { "Ignoring event $event" }
                    }
                }
            }
        }
    }

    private fun observeRecords() {
        viewModelScope.launch {
            measurementsRepository.observeBloodPressureRecord().collect { result ->
                _uiState.update { it.copy(bloodPressure = uiStateMapper.mapBloodPressure(result)) }
            }
        }

        viewModelScope.launch {
            measurementsRepository.observeHeartRateRecord().collect { result ->
                _uiState.update { it.copy(heartRate = uiStateMapper.mapHeartRate(result)) }
            }
        }

        viewModelScope.launch {
            measurementsRepository.observeWeightRecord().collect { result ->
                _uiState.update { it.copy(weight = uiStateMapper.mapWeight(result)) }
            }
        }
    }

    private fun observeMessages() {
        viewModelScope.launch {
            messagesHandler.observeUserMessages().collect { messages ->
                _uiState.update {
                    it.copy(
                        messages = messages
                    )
                }
            }
        }
    }

    fun onAction(action: Action) {
        when (action) {
            is Action.ConfirmMeasurement -> {
                handleConfirmMeasurementAction(action)
            }

            is Action.BLEDevicePairing -> {
                appScreenEvents.emit(AppScreenEvents.Event.BLEDevicePairingBottomSheet)
            }

            is Action.DismissDialog -> {
                _uiState.update {
                    it.copy(measurementDialog = it.measurementDialog.copy(isVisible = false))
                }
            }

            is Action.MessageItemClicked -> {
                handleMessage(message = action.message)
            }

            is Action.ToggleExpand -> {
                handleToggleExpandAction(action)
            }

            is Action.Resumed -> {
                bleService.start()
            }

            is Action.PermissionResult -> {
                _uiState.update { state ->
                    val missingPermission = state.missingPermissions.filterNot { it == action.permission }
                    state.copy(missingPermissions = missingPermission.toSet())
                }
                bleService.start()
            }

            is Action.Settings.AppSettings -> {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", context.packageName, null)
                }
                launch(intent = intent)
            }

            is Action.Settings.BluetoothSettings -> {
                launch(intent = Intent(Settings.ACTION_BLUETOOTH_SETTINGS))
            }

            is Action.VitalsCardClicked -> appScreenEvents.emit(
                AppScreenEvents.Event.NavigateToTab(
                    BottomBarItem.HEART_HEALTH
                )
            )
        }
    }

    private fun launch(intent: Intent) {
        runCatching {
            context.startActivity(intent.apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) })
        }.onFailure {
            logger.e(it) { "Failed to launch intent ${intent.action}" }
        }
    }

    private fun handleMessage(message: Message) {
        val setLoading = { loading: Boolean ->
            _uiState.update {
                it.copy(
                    messages = it.messages.map { current ->
                        if (current.id == message.id) {
                            current.copy(isLoading = loading)
                        } else {
                            current
                        }
                    }
                )
            }
        }
        viewModelScope.launch {
            setLoading(true)
            messagesHandler.handle(message = message)
            setLoading(false)
        }
    }

    public override fun onCleared() {
        super.onCleared()
        bleService.stop()
    }

    private fun handleToggleExpandAction(action: Action.ToggleExpand) {
        _uiState.update {
            it.copy(
                messages = it.messages.map { message ->
                    if (message.id == action.message.id) {
                        message.copy(isExpanded = !message.isExpanded)
                    } else {
                        message
                    }
                }
            )
        }
    }

    private fun handleConfirmMeasurementAction(action: Action.ConfirmMeasurement) {
        _uiState.update {
            it.copy(
                measurementDialog = it.measurementDialog.copy(
                    isProcessing = true,
                )
            )
        }
        viewModelScope.launch {
            measurementsRepository.save(measurement = action.measurement)
            _uiState.update {
                it.copy(
                    measurementDialog = it.measurementDialog.copy(
                        isVisible = false,
                        measurement = null,
                        isProcessing = false,
                    )
                )
            }
        }
    }
}