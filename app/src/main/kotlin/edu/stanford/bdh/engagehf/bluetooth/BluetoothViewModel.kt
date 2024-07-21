package edu.stanford.bdh.engagehf.bluetooth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.stanford.bdh.engagehf.bluetooth.component.BottomSheetEvents
import edu.stanford.bdh.engagehf.bluetooth.data.mapper.BluetoothUiStateMapper
import edu.stanford.bdh.engagehf.bluetooth.data.models.Action
import edu.stanford.bdh.engagehf.bluetooth.data.models.BluetoothUiState
import edu.stanford.bdh.engagehf.bluetooth.data.models.UiState
import edu.stanford.bdh.engagehf.education.EngageEducationRepository
import edu.stanford.bdh.engagehf.messages.MessageRepository
import edu.stanford.bdh.engagehf.messages.MessagesAction
import edu.stanford.spezi.core.bluetooth.api.BLEService
import edu.stanford.spezi.core.bluetooth.data.model.BLEServiceEvent
import edu.stanford.spezi.core.bluetooth.data.model.BLEServiceState
import edu.stanford.spezi.core.logging.speziLogger
import edu.stanford.spezi.core.navigation.Navigator
import edu.stanford.spezi.modules.education.EducationNavigationEvent
import edu.stanford.spezi.modules.measurements.MeasurementsRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
@Suppress("LongParameterList")
class BluetoothViewModel @Inject internal constructor(
    private val bleService: BLEService,
    private val uiStateMapper: BluetoothUiStateMapper,
    private val measurementsRepository: MeasurementsRepository,
    private val messageRepository: MessageRepository,
    private val bottomSheetEvents: BottomSheetEvents,
    private val navigator: Navigator,
    private val engageEducationRepository: EngageEducationRepository,
) : ViewModel() {
    private val logger by speziLogger()

    private val _events = MutableSharedFlow<Event>(replay = 1, extraBufferCapacity = 1)
    private val _uiState = MutableStateFlow(UiState())

    val events = _events.asSharedFlow()
    val uiState = _uiState.asStateFlow()

    init {
        observeBleService()
        observeRecords()
        observeMessages()
    }

    private fun observeBleService() {
        bleService.start()
        viewModelScope.launch {
            bleService.state.collect { state ->
                logger.i { "Received BLEService state $state" }
                when (state) {
                    BLEServiceState.Idle -> _uiState.update {
                        it.copy(bluetooth = BluetoothUiState.Idle)
                    }

                    is BLEServiceState.Scanning -> _uiState.update {
                        it.copy(bluetooth = uiStateMapper.mapBleServiceState(state))
                    }
                }
            }
        }
        viewModelScope.launch {
            bleService.events.collect { event ->
                logger.i { "Received BLEService event $event" }
                when (event) {
                    BLEServiceEvent.BluetoothNotEnabled -> _events.emit(Event.EnableBluetooth)
                    is BLEServiceEvent.MissingPermissions -> _events.emit(
                        Event.RequestPermissions(event.permissions)
                    )

                    is BLEServiceEvent.GenericError -> _uiState.update {
                        it.copy(bluetooth = BluetoothUiState.Error("Something went wrong!"))
                    }

                    is BLEServiceEvent.ScanningFailed -> _uiState.update {
                        it.copy(bluetooth = BluetoothUiState.Error("Error while scanning for devices"))
                    }

                    BLEServiceEvent.ScanningStarted -> _uiState.update {
                        it.copy(bluetooth = BluetoothUiState.Scanning)
                    }

                    is BLEServiceEvent.Connected, is BLEServiceEvent.Disconnected -> {
                        logger.i { "Ignoring $event as it will be handled via BLEService state" }
                    }

                    is BLEServiceEvent.MeasurementReceived -> {
                        bottomSheetEvents.emit(BottomSheetEvents.Event.CloseBottomSheet)
                        _uiState.update {
                            it.copy(
                                measurementDialog = uiStateMapper.mapToMeasurementDialogUiState(
                                    event.measurement
                                )
                            )
                        }
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
            messageRepository.observeUserMessages().collect { messages ->
                _uiState.update { it.copy(messages = messages) }
            }
        }
    }

    fun onAction(action: Action) {
        when (action) {
            is Action.ConfirmMeasurement -> {
                handleConfirmMeasurementAction(action)
            }

            is Action.DismissDialog -> {
                _uiState.update {
                    it.copy(measurementDialog = it.measurementDialog.copy(isVisible = false))
                }
            }

            is Action.MessageItemClicked -> {
                viewModelScope.launch {
                    val mappingResult = uiStateMapper.mapMessagesAction(action.message.action)
                    if (mappingResult.isSuccess) {
                        when (val mappedAction = mappingResult.getOrNull()!!) {
                            is MessagesAction.HealthSummaryAction -> { /* TODO */
                            }

                            is MessagesAction.MeasurementsAction -> {
                                bottomSheetEvents.emit(BottomSheetEvents.Event.DoNewMeasurement)
                            }

                            is MessagesAction.MedicationsAction -> { /* TODO */
                            }

                            is MessagesAction.QuestionnaireAction -> { /* TODO */
                            }

                            is MessagesAction.VideoSectionAction -> {
                                viewModelScope.launch {
                                    engageEducationRepository.getVideoBySectionAndVideoId(
                                        mappedAction.videoSectionVideo.videoSectionId,
                                        mappedAction.videoSectionVideo.videoId
                                    ).getOrNull()?.let { video ->
                                        navigator.navigateTo(
                                            EducationNavigationEvent.VideoSectionClicked(
                                                video = video
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    } else {
                        logger.e { "Error while mapping action: ${mappingResult.exceptionOrNull()}" }
                    }
                    _uiState.update { // TODO trigger firebase function action.message.id?.let { messageRepository.completeMessage(it) }
                        it.copy(messages = it.messages.filter { message -> message.id != action.message.id })
                    }
                }
            }

            is Action.ToggleExpand -> {
                handleToggleExpandAction(action)
            }
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

    interface Event {
        object EnableBluetooth : Event
        data class RequestPermissions(val permissions: List<String>) : Event
    }
}
