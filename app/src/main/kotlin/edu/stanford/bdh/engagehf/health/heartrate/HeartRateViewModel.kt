package edu.stanford.bdh.engagehf.health.heartrate

import androidx.health.connect.client.records.HeartRateRecord
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.stanford.bdh.engagehf.bluetooth.component.BottomSheetEvents
import edu.stanford.bdh.engagehf.health.HealthAction
import edu.stanford.bdh.engagehf.health.HealthRepository
import edu.stanford.bdh.engagehf.health.HealthUiState
import edu.stanford.bdh.engagehf.health.HealthUiStateMapper
import edu.stanford.bdh.engagehf.health.TimeRange
import edu.stanford.spezi.core.logging.speziLogger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
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
            healthRepository.observeHeartRateRecords().collect { result ->
                when (result.isFailure) {
                    true -> {
                        _uiState.update {
                            HealthUiState.Error("Failed to observe weight records")
                        }
                    }

                    false -> {
                        _uiState.update {
                            HealthUiState.Success(
                                uiStateMapper.mapToHealthData(
                                    records = result.getOrNull() as List<HeartRateRecord>,
                                    selectedTimeRange = TimeRange.DAILY
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    fun onAction(healthAction: HealthAction) {
        logger.i { "HeartRateViewModel action" }
        when (healthAction) {
            is HealthAction.AddRecord -> logger.i { "AddRecord" }
            is HealthAction.DeleteRecord -> logger.i { "DeleteRecord" }
            is HealthAction.DescriptionBottomSheet -> logger.i { "DescriptionBottomSheet" }
            is HealthAction.ToggleTimeRangeDropdown -> {
                _uiState.update {
                    uiStateMapper.mapToggleTimeRange(healthAction, it)
                }
            }

            is HealthAction.UpdateTimeRange -> {
                _uiState.update {
                    uiStateMapper.updateTimeRange(it, healthAction.timeRange)
                }
            }
        }
    }
}
