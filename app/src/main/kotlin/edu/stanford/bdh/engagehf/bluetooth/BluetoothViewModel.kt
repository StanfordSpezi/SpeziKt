package edu.stanford.bdh.engagehf.bluetooth

import androidx.health.connect.client.records.HeartRateRecord
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.stanford.bdh.engagehf.bluetooth.component.BottomSheetEvents
import edu.stanford.bdh.engagehf.bluetooth.component.OperationStatus
import edu.stanford.bdh.engagehf.bluetooth.data.mapper.BluetoothUiStateMapper
import edu.stanford.bdh.engagehf.bluetooth.data.models.Action
import edu.stanford.bdh.engagehf.bluetooth.data.models.BluetoothUiState
import edu.stanford.bdh.engagehf.bluetooth.data.models.UiState
import edu.stanford.bdh.engagehf.education.EngageEducationRepository
import edu.stanford.bdh.engagehf.messages.MessageActionMapper
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
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
@Suppress("TooManyFunctions", "LongParameterList")
class BluetoothViewModel @Inject internal constructor(
    private val bleService: BLEService,
    private val uiStateMapper: BluetoothUiStateMapper,
    private val measurementsRepository: MeasurementsRepository,
    private val messageRepository: MessageRepository,
    private val bottomSheetEvents: BottomSheetEvents,
    private val navigator: Navigator,
    private val engageEducationRepository: EngageEducationRepository,
    private val messageActionMapper: MessageActionMapper,
) : ViewModel() {
    private val logger by speziLogger()

    private val _events = MutableSharedFlow<Event>(replay = 1, extraBufferCapacity = 1)
    private val _uiState = MutableStateFlow(UiState())

    val events = _events.asSharedFlow()
    val uiState = _uiState.asStateFlow()

    private val dateFormatter by lazy {
        DateTimeFormatter.ofPattern(
            "dd.MM.yyyy, HH:mm", Locale.getDefault()
        )
    }

    init {
        start()
    }

    public override fun onCleared() {
        super.onCleared()
        bleService.stop()
    }

    private fun start() {
        bleService.start()
        viewModelScope.launch {
            bleService.state.collect { state ->
                logger.i { "Received BLEService state $state" }
                when (state) {
                    BLEServiceState.Idle -> _uiState.update {
                        it.copy(bluetooth = BluetoothUiState.Idle)
                    }

                    is BLEServiceState.Scanning -> _uiState.update {
                        it.copy(bluetooth = uiStateMapper.map(state))
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
                        handleMeasurementReceived(event)
                    }
                }
            }
        }

        viewModelScope.launch {
            loadBloodPressure()
        }

        viewModelScope.launch {
            loadHeartRate()
        }

        viewModelScope.launch {
            loadWeight()
        }

        viewModelScope.launch {
            messageRepository.observeUserMessages().collect { messages ->
                _uiState.update {
                    it.copy(messages = messages)
                }
            }
        }
    }

    private suspend fun loadHeartRate() {
        measurementsRepository.observeHeartRateRecord().collect { result ->
            when (result.isFailure) {
                true -> {
                    loadHeartRateIsFailure()
                }

                false -> {
                    logger.i { "Latest heart rate observation: ${result.getOrNull()}" }
                    val record = result.getOrNull()
                    if (record != null) {
                        _uiState.update { state ->
                            state.copy(
                                heartRate = state.heartRate.copy(
                                    title = "Heart Rate",
                                    value = "${
                                        record.samples.stream()
                                            .mapToLong(HeartRateRecord.Sample::beatsPerMinute)
                                            .average()
                                            .orElse(Double.NaN)
                                    }",
                                    unit = "bpm",
                                    date = dateFormatter.format(
                                        record.samples.first().time.atZone(
                                            record.startZoneOffset
                                        )
                                    ),
                                    status = OperationStatus.SUCCESS
                                )
                            )
                        }
                    } else {
                        loadHeartRateNoData()
                    }
                }
            }
        }
    }

    private fun loadHeartRateNoData() {
        _uiState.update { state ->
            state.copy(
                heartRate = state.heartRate.copy(
                    title = "Heart Rate",
                    value = "No data available",
                    unit = null,
                    date = null,
                    status = OperationStatus.NO_DATA
                )
            )
        }
    }

    private fun loadHeartRateIsFailure() {
        logger.e { "Error while getting latest heart rate observation" }
        _uiState.update { state ->
            state.copy(
                heartRate = state.heartRate.copy(
                    status = OperationStatus.FAILURE,
                    date = null,
                    value = null,
                    unit = null
                )
            )
        }
    }

    private suspend fun loadBloodPressure() {
        measurementsRepository.observeBloodPressureRecord().collect { result ->
            when (result.isFailure) {
                true -> {
                    loadBloodPressureIsFailure()
                }

                false -> {
                    logger.i { "Latest blood pressure observation: ${result.getOrNull()}" }
                    val record = result.getOrNull()
                    if (record != null) {
                        _uiState.update { state ->
                            state.copy(
                                bloodPressure = state.bloodPressure.copy(
                                    title = "Blood Pressure",
                                    value =
                                    "${record.systolic.inMillimetersOfMercury}/${record.diastolic.inMillimetersOfMercury}",
                                    unit = "mmHg",
                                    date = dateFormatter.format(record.time.atZone(record.zoneOffset)),
                                    status = OperationStatus.SUCCESS
                                )
                            )
                        }
                    } else {
                        loadBloodPressureNoDataAvailable()
                    }
                }
            }
        }
    }

    private fun loadBloodPressureNoDataAvailable() {
        _uiState.update { state ->
            state.copy(
                bloodPressure = state.bloodPressure.copy(
                    title = "Blood Pressure",
                    status = OperationStatus.NO_DATA,
                    date = null,
                    value = "No data available",
                    unit = null,
                )
            )
        }
    }

    private fun loadBloodPressureIsFailure() {
        logger.e { "Error while getting latest blood pressure observation" }
        _uiState.update { state ->
            state.copy(
                bloodPressure = state.bloodPressure.copy(
                    title = "Blood Pressure",
                    status = OperationStatus.FAILURE,
                    date = null,
                    value = null,
                    unit = null,
                )
            )
        }
    }

    private suspend fun loadWeight() {
        measurementsRepository.observeWeightRecord().collect { result ->
            when (result.isFailure) {
                true -> {
                    logger.e { "Error while getting latest body weight observation" }
                    _uiState.update { state ->
                        state.copy(
                            weight = state.weight.copy(
                                title = "Weight",
                                status = OperationStatus.FAILURE,
                                date = null,
                                value = null,
                                unit = null,
                            )
                        )
                    }
                }

                false -> {
                    logger.i { "Latest body weight observation: ${result.getOrNull()}" }
                    val record = result.getOrNull()
                    if (record != null) {
                        _uiState.update { state ->
                            state.copy(
                                weight =
                                state.weight.copy(
                                    title = "Weight",
                                    value = String.format(
                                        Locale.getDefault(),
                                        "%.2f",
                                        when (Locale.getDefault().country) {
                                            "US", "LR", "MM" -> record.weight.inPounds
                                            else -> record.weight.inKilograms
                                        }
                                    ),
                                    unit = when (Locale.getDefault().country) {
                                        "US", "LR", "MM" -> "lbs"
                                        else -> "kg"
                                    },
                                    date = dateFormatter.format(record.time.atZone(record.zoneOffset)),
                                    status = OperationStatus.SUCCESS,
                                )
                            )
                        }
                    } else {
                        loadWeightFailure()
                    }
                }
            }
        }
    }

    private fun loadWeightFailure() {
        _uiState.update { state ->
            state.copy(
                weight = state.weight.copy(
                    title = "Weight",
                    value = null,
                    unit = null,
                    date = null,
                    status = OperationStatus.FAILURE
                )
            )
        }
    }

    fun onAction(action: Action) {
        when (action) {
            is Action.ConfirmMeasurement -> {
                handleConfirmMeasurementAction(action)
            }

            is Action.DismissDialog -> {
                _uiState.update {
                    it.copy(
                        measurementDialog = it.measurementDialog.copy(
                            isVisible = false,
                        )
                    )
                }
            }

            is Action.MessageItemClicked -> {
                viewModelScope.launch {
                    action.message.action.let {
                        val mappingResult = messageActionMapper.map(it)
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
                                        ).getOrThrow().let { video ->
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
            measurementsRepository.save(action.measurement)
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

    private fun handleMeasurementReceived(event: BLEServiceEvent.MeasurementReceived) {
        _uiState.update {
            bottomSheetEvents.emit(BottomSheetEvents.Event.CloseBottomSheet)
            it.copy(measurementDialog = uiStateMapper.mapToMeasurementDialogUiState(event.measurement))
        }
    }

    interface Event {
        object EnableBluetooth : Event
        data class RequestPermissions(val permissions: List<String>) : Event
    }
}
