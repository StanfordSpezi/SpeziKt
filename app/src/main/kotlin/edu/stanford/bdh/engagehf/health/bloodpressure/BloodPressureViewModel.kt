package edu.stanford.bdh.engagehf.health.bloodpressure

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
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
class BloodPressureViewModel @Inject internal constructor(
    private val uiStateMapper: HealthUiStateMapper,
    private val healthRepository: HealthRepository,
) : ViewModel() {
    private val logger by speziLogger()

    private val _uiState = MutableStateFlow<HealthUiState>(HealthUiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        setup()
    }

    private fun setup() {
        viewModelScope.launch {
            healthRepository.observeBloodPressureRecords().collect { result ->
                result.onFailure {
                    _uiState.update {
                        HealthUiState.Error("Failed to observe records")
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
        when (healthAction) {
            HealthAction.AddRecord -> logger.i { "AddRecord" }
            is HealthAction.DeleteRecord -> logger.i { "DeleteRecord" }
            HealthAction.DescriptionBottomSheet -> logger.i { "DescriptionBottomSheet" }
            is HealthAction.ToggleTimeRangeDropdown -> {
                _uiState.update {
                    uiStateMapper.mapToggleTimeRange(healthAction, it)
                }
            }

            is HealthAction.UpdateTimeRange -> {
                _uiState.update {
                    uiStateMapper.updateTimeRange(uiState.value, healthAction.timeRange)
                }
            }
        }
    }
}
