package edu.stanford.bdh.engagehf.health.heartrate

import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.records.Record
import androidx.health.connect.client.records.metadata.Metadata
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.stanford.bdh.engagehf.bluetooth.component.BottomSheetEvents
import edu.stanford.bdh.engagehf.health.HealthAction
import edu.stanford.bdh.engagehf.health.HealthRepository
import edu.stanford.bdh.engagehf.health.HealthUiState
import edu.stanford.bdh.engagehf.health.HealthUiStateMapper
import edu.stanford.bdh.engagehf.health.TimeRange
import edu.stanford.bdh.engagehf.health.weight.WeightViewModel
import edu.stanford.spezi.core.logging.speziLogger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.ZonedDateTime
import javax.inject.Inject

@HiltViewModel
class HeartRateViewModel @Inject internal constructor(
    private val bottomSheetEvents: BottomSheetEvents,
    private val uiStateMapper: HealthUiStateMapper<HeartRateRecord>,
    private val healthRepository: HealthRepository,
) : ViewModel() {
    private val logger by speziLogger()

    private val _uiState = MutableStateFlow<HealthUiState>(HealthUiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        setup()
    }

    fun setup() {
        viewModelScope.launch {
            healthRepository.observeHeartRateRecords(
                ZonedDateTime.now(),
                ZonedDateTime.now().minusMonths(WeightViewModel.DEFAULT_MAX_MONTHS)
            ).collect { result ->
                when (result.isFailure) {
                    true -> {
                        _uiState.update {
                            HealthUiState.Error("Failed to observe weight records")
                        }
                    }

                    false -> {
                        val heartRateRecords: List<Record> = listOf(
                            HeartRateRecord(
                                startTime = ZonedDateTime.now().toInstant(),
                                startZoneOffset = ZonedDateTime.now().offset,
                                endTime = ZonedDateTime.now().toInstant(),
                                endZoneOffset = ZonedDateTime.now().offset,
                                samples = listOf(
                                    HeartRateRecord.Sample(
                                        ZonedDateTime.now().toInstant(),
                                        60L,
                                    )
                                ),
                                metadata = Metadata(
                                    clientRecordId = "1",
                                )
                            )
                        )

                        _uiState.update {
                            HealthUiState.Success(
                                uiStateMapper.mapToHealthData(
                                    records = heartRateRecords as List<HeartRateRecord>,
                                    selectedTimeRange = TimeRange.DAILY
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    companion object {
        private const val DEFAULT_MAX_MONTHS = 6L
    }

    fun onAction(healthAction: HealthAction) {
        logger.i { "HeartRateViewModel action" }
        when (healthAction) {
            is HealthAction.AddRecord -> TODO()
            is HealthAction.DeleteRecord -> TODO()
            is HealthAction.DescriptionBottomSheet -> TODO()
            is HealthAction.ToggleTimeRangeDropdown -> TODO()
            is HealthAction.UpdateTimeRange -> when (val uiState = _uiState.value) {
                is HealthUiState.Loading -> return
                is HealthUiState.Error -> return
                is HealthUiState.Success -> {
                    _uiState.update {
                        uiStateMapper.updateTimeRange(uiState, healthAction.timeRange)
                    }
                }
            }
        }
    }
}
