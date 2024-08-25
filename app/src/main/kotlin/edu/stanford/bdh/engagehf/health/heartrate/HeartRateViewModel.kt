package edu.stanford.bdh.engagehf.health.heartrate

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.stanford.bdh.engagehf.bluetooth.component.AppScreenEvents
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
    private val uiStateMapper: HealthUiStateMapper,
    private val healthRepository: HealthRepository,
    private val appScreenEvents: AppScreenEvents,
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
                result.onFailure {
                    _uiState.update {
                        HealthUiState.Error("Failed to observe weight records")
                    }
                }.onSuccess { records ->
                    _uiState.update {
                        uiStateMapper.mapToHealthData(
                            records = records,
                            selectedTimeRange = TimeRange.DAILY
                        )
                    }
                }
            }
        }
    }

    fun onAction(healthAction: HealthAction) {
        logger.i { "HeartRateViewModel action" }
        when (healthAction) {
            is HealthAction.DeleteRecord -> {
                viewModelScope.launch {
                    healthRepository.deleteHeartRateRecord(healthAction.recordId)
                }
            }

            is HealthAction.DescriptionBottomSheet -> {
                appScreenEvents.emit(AppScreenEvents.Event.HeartRateDescriptionBottomSheet)
            }

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
