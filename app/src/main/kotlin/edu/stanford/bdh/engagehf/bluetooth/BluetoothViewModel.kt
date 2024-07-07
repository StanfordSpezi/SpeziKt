package edu.stanford.bdh.engagehf.bluetooth

import androidx.health.connect.client.records.HeartRateRecord
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.stanford.bdh.engagehf.bluetooth.component.OperationStatus
import edu.stanford.bdh.engagehf.bluetooth.data.mapper.BluetoothUiStateMapper
import edu.stanford.bdh.engagehf.bluetooth.data.mapper.MeasurementToRecordMapper
import edu.stanford.bdh.engagehf.bluetooth.data.models.Action
import edu.stanford.bdh.engagehf.bluetooth.data.models.BluetoothUiState
import edu.stanford.bdh.engagehf.bluetooth.data.models.MeasurementDialogUiState
import edu.stanford.bdh.engagehf.bluetooth.data.models.VitalDisplayData
import edu.stanford.bdh.engagehf.bluetooth.data.models.VitalDisplayUiState
import edu.stanford.bdh.engagehf.bluetooth.data.repository.ObservationRepository
import edu.stanford.healthconnectonfhir.RecordToObservationMapper
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
    private val recordToObservation: RecordToObservationMapper,
    private val observationRepository: ObservationRepository,
    private val measurementToRecordMapper: MeasurementToRecordMapper,
) : ViewModel() {
    private val logger by speziLogger()

    private val _events = MutableSharedFlow<Event>(replay = 1, extraBufferCapacity = 1)
    private val _uiState = MutableStateFlow<BluetoothUiState>(BluetoothUiState.Idle)
    private val _dialogUiState = MutableStateFlow(MeasurementDialogUiState())
    private val _vitalDisplayUiState = MutableStateFlow(VitalDisplayUiState())

    val uiState = _uiState.asStateFlow()
    val events = _events.asSharedFlow()
    val dialogUiState = _dialogUiState.asStateFlow()
    val vitalDisplayUiState = _vitalDisplayUiState.asStateFlow()

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
                    is BLEServiceEvent.MissingPermissions -> _events.emit(
                        Event.RequestPermissions(
                            event.permissions
                        )
                    )

                    is BLEServiceEvent.GenericError -> _uiState.update { BluetoothUiState.Error("Something went wrong!") }
                    is BLEServiceEvent.ScanningFailed -> _uiState.update { BluetoothUiState.Error("Error while scanning for devices") }
                    BLEServiceEvent.ScanningStarted -> _uiState.update { BluetoothUiState.Scanning }
                    is BLEServiceEvent.Connected, is BLEServiceEvent.Disconnected, is BLEServiceEvent.MeasurementReceived -> {
                        logger.i { "Ignoring $event as it will be handled via BLEService state" }
                        handleMeasurement(event)
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
    }

    private suspend fun loadHeartRate() {
        val result = observationRepository.getLatestHeartRateObservation()
        when (result.isFailure) {
            true -> {
                logger.e { "Error while getting latest heart rate observation" }
                _vitalDisplayUiState.update { state ->
                    state.copy(
                        heartRate = state.heartRate.copy(
                            status = OperationStatus.FAILURE, date = null, value = null, unit = null
                        )
                    )
                }
            }

            false -> {
                logger.i { "Latest heart rate observation: ${result.getOrNull()}" }
                val record = result.getOrNull()
                if (record != null) {
                    _vitalDisplayUiState.update { state ->
                        state.copy(
                            heartRate = VitalDisplayData(
                                title = "Heart Rate", value = "${
                                    record.samples.stream()
                                        .mapToLong(HeartRateRecord.Sample::beatsPerMinute).average()
                                        .orElse(Double.NaN)
                                }", unit = "bpm", date = dateFormatter.format(
                                    record.samples.first().time.atZone(
                                        record.startZoneOffset
                                    )
                                ), status = OperationStatus.SUCCESS
                            )
                        )
                    }
                } else {
                    _vitalDisplayUiState.update { state ->
                        state.copy(
                            heartRate = state.heartRate.copy(
                                status = OperationStatus.NO_DATA,
                                date = null,
                                value = "No data available",
                                unit = null
                            )
                        )
                    }
                }
            }
        }
    }

    private suspend fun loadBloodPressure() {
        val result = observationRepository.getLatestBloodPressureObservation()
        when (result.isFailure) {
            true -> {
                logger.e { "Error while getting latest blood pressure observation" }
                _vitalDisplayUiState.update { state ->
                    state.copy(
                        bloodPressure = state.bloodPressure.copy(
                            status = OperationStatus.FAILURE, date = null, value = null, unit = null
                        )
                    )
                }
            }

            false -> {
                logger.i { "Latest blood pressure observation: ${result.getOrNull()}" }
                val record = result.getOrNull()
                if (record != null) {
                    _vitalDisplayUiState.update { state ->
                        state.copy(
                            bloodPressure = VitalDisplayData(
                                title = "Blood Pressure",
                                value = "${record.systolic.inMillimetersOfMercury}/${record.diastolic.inMillimetersOfMercury}",
                                unit = "mmHg",
                                date = dateFormatter.format(record.time.atZone(record.zoneOffset)),
                                status = OperationStatus.SUCCESS
                            )
                        )
                    }
                } else {
                    _vitalDisplayUiState.update { state ->
                        state.copy(
                            bloodPressure = state.bloodPressure.copy(
                                status = OperationStatus.FAILURE,
                                date = null,
                                value = null,
                                unit = null
                            )
                        )
                    }
                }
            }
        }
    }

    private suspend fun loadWeight() {
        val result = observationRepository.getLatestBodyWeightObservation()
        when (result.isFailure) {
            true -> {
                logger.e { "Error while getting latest body weight observation" }
                _vitalDisplayUiState.update { state ->
                    state.copy(
                        weight = state.weight.copy(
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
                    _vitalDisplayUiState.update { state ->
                        state.copy(
                            weight = VitalDisplayData(
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
                                status = OperationStatus.SUCCESS
                            )
                        )
                    }
                } else {
                    _vitalDisplayUiState.update { state ->
                        state.copy(
                            weight = state.weight.copy(
                                status = OperationStatus.FAILURE,
                                date = null,
                                value = null,
                                unit = null
                            )
                        )
                    }
                }
            }
        }
    }

    fun onAction(action: Action) {
        when (action) {
            is Action.ConfirmMeasurement -> {
                _dialogUiState.update {
                    it.copy(isProcessing = true)
                }
                viewModelScope.launch {
                    if (action.measurement is Measurement.Weight) {
                        val records = measurementToRecordMapper.map(action.measurement)
                        records.forEach { record ->
                            recordToObservation.map(record).let {
                                observationRepository.saveObservations(it)
                            }
                        }
                        loadWeight()
                    }

                    if (action.measurement is Measurement.BloodPressure) {
                        val records = measurementToRecordMapper.map(action.measurement)
                        records.forEach { record ->
                            recordToObservation.map(record).let {
                                observationRepository.saveObservations(it)
                            }
                        }
                        loadHeartRate()
                        loadBloodPressure()
                    }

                    _dialogUiState.update {
                        it.copy(
                            isVisible = false,
                            measurement = null,
                            isProcessing = false,
                        )
                    }
                }
            }

            is Action.DismissDialog -> {
                _dialogUiState.update {
                    it.copy(isVisible = false)
                }
            }
        }
    }

    private fun handleMeasurement(event: BLEServiceEvent) {
        if (event is BLEServiceEvent.MeasurementReceived) {
            if (event.measurement is Measurement.Weight) {
                _dialogUiState.update {
                    val weight = (event.measurement as Measurement.Weight).weight
                    val weightInPounds = weight * KG_TO_LBS_CONVERSION_FACTOR
                    it.copy(
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
                }
            }
            if (event.measurement is Measurement.BloodPressure) {
                _dialogUiState.update {
                    it.copy(measurement = event.measurement, isVisible = true)
                }
            }
        }
    }

    interface Event {
        object EnableBluetooth : Event
        data class RequestPermissions(val permissions: List<String>) : Event
    }
}
