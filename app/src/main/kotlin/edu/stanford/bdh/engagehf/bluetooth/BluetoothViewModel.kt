package edu.stanford.bdh.engagehf.bluetooth

import androidx.health.connect.client.records.HeartRateRecord
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.stanford.bdh.engagehf.bluetooth.component.OperationStatus
import edu.stanford.bdh.engagehf.bluetooth.data.mapper.BluetoothUiStateMapper
import edu.stanford.bdh.engagehf.bluetooth.data.mapper.MeasurementToObservationMapper
import edu.stanford.bdh.engagehf.bluetooth.data.models.Action
import edu.stanford.bdh.engagehf.bluetooth.data.models.BluetoothUiState
import edu.stanford.bdh.engagehf.bluetooth.data.models.UiState
import edu.stanford.bdh.engagehf.bluetooth.data.repository.ObservationRepository
import edu.stanford.bdh.engagehf.messages.MessageRepository
import edu.stanford.bdh.engagehf.messages.MessageType
import edu.stanford.spezi.core.bluetooth.api.BLEService
import edu.stanford.spezi.core.bluetooth.data.model.BLEServiceEvent
import edu.stanford.spezi.core.bluetooth.data.model.BLEServiceState
import edu.stanford.spezi.core.bluetooth.data.model.Measurement
import edu.stanford.spezi.core.logging.speziLogger
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
class BluetoothViewModel @Inject internal constructor(
    private val bleService: BLEService,
    private val uiStateMapper: BluetoothUiStateMapper,
    private val observationRepository: ObservationRepository,
    private val messageRepository: MessageRepository,
    private val measurementToObservationMapper: MeasurementToObservationMapper,
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

    companion object {
        const val KG_TO_LBS_CONVERSION_FACTOR = 2.20462
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
            loadWeight()
        }

        viewModelScope.launch {
            loadHeartRate()
        }

        viewModelScope.launch {
            messageRepository.listenForUserMessages { messages ->
                _uiState.update {
                    it.copy(messages = messages + it.messages)
                }
            }
        }
    }

    private suspend fun loadHeartRate() {
        observationRepository.listenForLatestHeartRateObservation { result ->
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
        observationRepository.listenForLatestBloodPressureObservation { result ->
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
        observationRepository.listenForLatestBodyWeightObservation { result ->
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
                    action.message.type?.let { type ->
                        when (type) {
                            MessageType.MedicationChange -> {} // TODO needs to be defined
                            MessageType.WeightGain -> {} // TODO needs to be defined
                            MessageType.MedicationUptitration -> {} // TODO needs to be defined
                            MessageType.Welcome -> {} // TODO needs to be defined
                            MessageType.Vitals -> {} // TODO needs to be defined
                            MessageType.SymptomQuestionnaire -> {} // TODO needs to be defined
                            MessageType.PreVisit -> {} // TODO needs to be defined
                        }
                    }
                    action.message.id?.let { messageRepository.completeMessage(it) }
                    _uiState.update {
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
            if (action.measurement is Measurement.Weight) {
                measurementToObservationMapper.map(action.measurement).let {
                    observationRepository.saveObservations(it)
                }
            }

            if (action.measurement is Measurement.BloodPressure) {
                measurementToObservationMapper.map(action.measurement).let {
                    observationRepository.saveObservations(it)
                }
            }
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

    private fun handleMeasurementReceived(event: BLEServiceEvent) {
        if (event is BLEServiceEvent.MeasurementReceived) {
            if (event.measurement is Measurement.Weight) {
                _uiState.update {
                    val weight = (event.measurement as Measurement.Weight).weight
                    val weightInPounds = weight * KG_TO_LBS_CONVERSION_FACTOR
                    it.copy(
                        measurementDialog = it.measurementDialog.copy(
                            measurement = event.measurement,
                            isVisible = true,
                            formattedWeight = String.format(
                                Locale.getDefault(), "%.2f", when (Locale.getDefault().country) {
                                    "US", "LR", "MM" -> weightInPounds
                                    else -> weight
                                }
                            ) + when (Locale.getDefault().country) {
                                "US", "LR", "MM" -> "lbs"
                                else -> "kg"
                            }
                        )
                    )
                }
            }
            if (event.measurement is Measurement.BloodPressure) {
                _uiState.update {
                    it.copy(
                        measurementDialog = it.measurementDialog.copy(
                            measurement = event.measurement,
                            isVisible = true
                        )
                    )
                }
            }
        }
    }

    interface Event {
        object EnableBluetooth : Event
        data class RequestPermissions(val permissions: List<String>) : Event
    }
}
