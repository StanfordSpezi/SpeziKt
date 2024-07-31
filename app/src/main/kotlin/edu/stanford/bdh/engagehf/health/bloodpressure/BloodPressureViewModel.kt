package edu.stanford.bdh.engagehf.health.bloodpressure

import androidx.health.connect.client.records.BloodPressureRecord
import androidx.health.connect.client.records.Record
import androidx.health.connect.client.records.metadata.Metadata
import androidx.health.connect.client.units.Pressure
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.stanford.bdh.engagehf.bluetooth.component.BottomSheetEvents
import edu.stanford.bdh.engagehf.health.HealthRepository
import edu.stanford.bdh.engagehf.health.TimeRange
import edu.stanford.bdh.engagehf.health.weight.Action
import edu.stanford.bdh.engagehf.health.weight.HealthUiState
import edu.stanford.bdh.engagehf.health.weight.HealthUiStateMapper
import edu.stanford.bdh.engagehf.health.weight.WeightViewModel.Companion.DEFAULT_MAX_MONTHS
import edu.stanford.spezi.core.logging.speziLogger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.ZonedDateTime
import javax.inject.Inject

@HiltViewModel
class BloodPressureViewModel @Inject internal constructor(
    private val bottomSheetEvents: BottomSheetEvents,
    private val uiStateMapper: HealthUiStateMapper,
    private val healthRepository: HealthRepository,
) : ViewModel() {
    private val logger by speziLogger()

    private val _uiState = MutableStateFlow<HealthUiState>(HealthUiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        println()
        setup()
    }

    private fun setup() {
        viewModelScope.launch {
            healthRepository.observeWeightRecords(
                ZonedDateTime.now(), ZonedDateTime.now().minusMonths(DEFAULT_MAX_MONTHS)
            ).collect { result ->
                when (result.isFailure) {
                    true -> {
                        _uiState.update {
                            HealthUiState.Error("Failed to observe weight records")
                        }
                    }

                    false -> {
                        val bloodPressure: List<Record> = listOf(
                            BloodPressureRecord(
                                time = ZonedDateTime.now().toInstant(),
                                zoneOffset = ZonedDateTime.now().offset,
                                systolic = Pressure.millimetersOfMercury(120.0),
                                diastolic = Pressure.millimetersOfMercury(80.0),
                                metadata = Metadata(clientRecordId = "1")
                            ),
                            BloodPressureRecord(
                                time = ZonedDateTime.now().minusDays(1).toInstant(),
                                zoneOffset = ZonedDateTime.now().offset,
                                systolic = Pressure.millimetersOfMercury(130.0),
                                diastolic = Pressure.millimetersOfMercury(90.0),
                                metadata = Metadata(clientRecordId = "2")
                            ),
                            BloodPressureRecord(
                                time = ZonedDateTime.now().minusDays(5).toInstant(),
                                zoneOffset = ZonedDateTime.now().offset,
                                systolic = Pressure.millimetersOfMercury(140.0),
                                diastolic = Pressure.millimetersOfMercury(100.0),
                                metadata = Metadata(clientRecordId = "3")
                            ),
                        )

                        _uiState.update {
                            HealthUiState.Success(
                                uiStateMapper.mapToHealthData(
                                    records = bloodPressure,
                                    selectedTimeRange = TimeRange.DAILY
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    fun onAction(action: Action) {
        println()
    }
}
