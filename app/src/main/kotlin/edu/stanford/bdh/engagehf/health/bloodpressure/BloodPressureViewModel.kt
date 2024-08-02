package edu.stanford.bdh.engagehf.health.bloodpressure

import androidx.health.connect.client.records.BloodPressureRecord
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
class BloodPressureViewModel @Inject internal constructor(
    private val bottomSheetEvents: BottomSheetEvents,
    private val uiStateMapper: HealthUiStateMapper<BloodPressureRecord>,
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
            healthRepository.observeBloodPressureRecords(
                ZonedDateTime.now(), ZonedDateTime.now().minusMonths(DEFAULT_MAX_MONTHS)
            ).collect { result ->
                when (result.isFailure) {
                    true -> {
                        _uiState.update {
                            HealthUiState.Error("Failed to observe records")
                        }
                    }

                    false -> {
                        _uiState.update {
                            HealthUiState.Success(
                                uiStateMapper.mapToHealthData(
                                    records = result.getOrNull() as List<BloodPressureRecord>,
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
            HealthAction.AddRecord -> logger.i { "AddRecord" }
            is HealthAction.DeleteRecord -> logger.i { "DeleteRecord" }
            HealthAction.DescriptionBottomSheet -> logger.i { "DescriptionBottomSheet" }
            is HealthAction.ToggleTimeRangeDropdown -> {
                _uiState.update {
                    uiStateMapper.mapToggleTimeRange(healthAction, it)
                }
            }

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
