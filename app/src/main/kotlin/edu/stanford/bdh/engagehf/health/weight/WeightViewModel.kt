@file:Suppress("MagicNumber")

package edu.stanford.bdh.engagehf.health.weight

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
class WeightViewModel @Inject internal constructor(
    private val bottomSheetEvents: BottomSheetEvents,
    private val uiStateMapper: HealthUiStateMapper,
    private val healthRepository: HealthRepository,
) : ViewModel() {
    private val logger by speziLogger()

    private val _uiState = MutableStateFlow<HealthUiState>(HealthUiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        setup()
        logger.i { "WeightViewModel initialized" }
    }

    private fun setup() {
        viewModelScope.launch {
            healthRepository.observeWeightRecords().collect { result ->
                result.onFailure {
                    _uiState.update {
                        HealthUiState.Error("Failed to observe weight records")
                    }
                }.onSuccess { successResult ->
                    _uiState.update {
                        uiStateMapper.mapToHealthData(
                            records = successResult,
                            selectedTimeRange = TimeRange.DAILY
                        )
                    }
                }
            }
        }
    }

    fun onAction(healthAction: HealthAction) {
        when (healthAction) {
            HealthAction.AddRecord -> {
                bottomSheetEvents.emit(BottomSheetEvents.Event.AddWeightRecord)
            }

            is HealthAction.DeleteRecord -> {
                logger.i { "WeightViewModel.onAction Delete Weight Record: ${healthAction.recordId}" }
            }

            is HealthAction.UpdateTimeRange -> {
                _uiState.update {
                    uiStateMapper.updateTimeRange(it, healthAction.timeRange)
                }
            }

            HealthAction.DescriptionBottomSheet -> {
                bottomSheetEvents.emit(BottomSheetEvents.Event.WeightDescriptionBottomSheet)
            }

            is HealthAction.ToggleTimeRangeDropdown -> {
                _uiState.update {
                    uiStateMapper.mapToggleTimeRange(healthAction, it)
                }
            }
        }
    }
}
