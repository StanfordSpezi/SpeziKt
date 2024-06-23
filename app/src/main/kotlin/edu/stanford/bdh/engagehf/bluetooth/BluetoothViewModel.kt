package edu.stanford.bdh.engagehf.bluetooth

import androidx.health.connect.client.records.BloodPressureRecord
import androidx.health.connect.client.records.WeightRecord
import androidx.health.connect.client.units.Mass
import androidx.health.connect.client.units.Pressure
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.stanford.bdh.engagehf.bluetooth.data.mapper.BluetoothUiStateMapper
import edu.stanford.bdh.engagehf.bluetooth.data.models.Action
import edu.stanford.bdh.engagehf.bluetooth.data.models.BluetoothUiState
import edu.stanford.bdh.engagehf.bluetooth.data.models.MeasurementDialogUiState
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
import java.time.LocalDateTime
import java.time.ZonedDateTime
import javax.inject.Inject

@HiltViewModel
class BluetoothViewModel @Inject internal constructor(
    private val bleService: BLEService,
    private val uiStateMapper: BluetoothUiStateMapper,
    private val recordToObservation: RecordToObservationMapper,
    private val observationRepository: ObservationRepository,
) : ViewModel() {
    private val logger by speziLogger()

    private val _events = MutableSharedFlow<Event>(replay = 1, extraBufferCapacity = 1)
    private val _uiState = MutableStateFlow<BluetoothUiState>(BluetoothUiState.Idle)
    private val _dialogUiState = MutableStateFlow(MeasurementDialogUiState())

    val uiState = _uiState.asStateFlow()
    val events = _events.asSharedFlow()
    val dialogUiState = _dialogUiState.asStateFlow()

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
    }

    fun onAction(action: Action) {
        when (action) {
            is Action.ConfirmMeasurement -> {
                viewModelScope.launch {
                    if (action.measurement is Measurement.Weight) {
                        val record = createWeightRecord(action.measurement)
                        recordToObservation.map(record).let {
                            observationRepository.saveObservations(it)
                        }
                    }

                    if (action.measurement is Measurement.BloodPressure) {
                        val record = createBloodPressureRecord(action.measurement)
                        recordToObservation.map(record).let {
                            observationRepository.saveObservations(it)
                        }
                    }

                    _dialogUiState.update {
                        it.copy(isVisible = false, measurement = null)
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
            if (event.measurement is Measurement.Weight || event.measurement is Measurement.BloodPressure) {
                _dialogUiState.update {
                    it.copy(measurement = event.measurement, isVisible = true)
                }
            }
        }
    }

    private fun createBloodPressureRecord(measurement: Measurement.BloodPressure): BloodPressureRecord {
        // TODO create a mapper for bt measurements to health connect
        return BloodPressureRecord(
            systolic = Pressure.millimetersOfMercury(measurement.systolic.toDouble()),
            diastolic = Pressure.millimetersOfMercury(measurement.diastolic.toDouble()),
            time = LocalDateTime.of(
                measurement.timestampYear,
                measurement.timestampMonth,
                measurement.timestampDay,
                measurement.timeStampHour,
                measurement.timeStampMinute,
                measurement.timeStampSecond
            ).toInstant(
                ZonedDateTime.now().offset
            ),
            zoneOffset = ZonedDateTime.now().offset
        )
    }

    private fun createWeightRecord(measurement: Measurement.Weight): WeightRecord {
        return WeightRecord(
            // TODO consider making weight no more nullable in the model
            weight = Mass.kilograms(measurement.weight ?: 0.0),
            time = measurement.zonedDateTime!!.toInstant(),
            zoneOffset = measurement.zonedDateTime!!.offset
        )
    }

    interface Event {
        object EnableBluetooth : Event
        data class RequestPermissions(val permissions: List<String>) : Event
    }
}
