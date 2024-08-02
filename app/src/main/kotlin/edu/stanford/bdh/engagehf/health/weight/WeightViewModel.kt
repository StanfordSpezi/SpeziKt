@file:Suppress("MagicNumber")

package edu.stanford.bdh.engagehf.health.weight

import androidx.health.connect.client.records.WeightRecord
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.stanford.bdh.engagehf.bluetooth.component.BottomSheetEvents
import edu.stanford.bdh.engagehf.health.HealthAction
import edu.stanford.bdh.engagehf.health.HealthRepository
import edu.stanford.bdh.engagehf.health.HealthRepository.Companion.DEFAULT_MAX_MONTHS
import edu.stanford.bdh.engagehf.health.HealthUiState
import edu.stanford.bdh.engagehf.health.HealthUiStateMapper
import edu.stanford.bdh.engagehf.health.TimeRange
import edu.stanford.spezi.core.logging.speziLogger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.ZonedDateTime
import javax.inject.Inject

@HiltViewModel
class WeightViewModel @Inject internal constructor(
    private val bottomSheetEvents: BottomSheetEvents,
    private val uiStateMapper: HealthUiStateMapper<WeightRecord>,
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
                        _uiState.update {
                            HealthUiState.Success(
                                uiStateMapper.mapToHealthData(
                                    records = result.getOrNull() as List<WeightRecord>,
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
        when (healthAction) {
            HealthAction.AddRecord -> {
                bottomSheetEvents.emit(BottomSheetEvents.Event.AddWeightRecord)
            }

            is HealthAction.DeleteRecord -> {
                // TODO
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
