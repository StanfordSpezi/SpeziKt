package edu.stanford.bdh.engagehf.health

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.stanford.bdh.engagehf.bluetooth.component.BottomSheetEvents
import edu.stanford.spezi.core.logging.speziLogger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.ZonedDateTime
import javax.inject.Inject

@HiltViewModel
class HealthViewModel @Inject internal constructor(
    private val bottomSheetEvents: BottomSheetEvents,
    private val healthUiStateMapper: HealthUiStateMapper,
    private val healthRepository: HealthRepository,
) : ViewModel() {
    private val logger by speziLogger()

    private val _uiState = MutableStateFlow(HealthUiState())
    val uiState get() = _uiState.asStateFlow()

    init {
        setup()
        logger.i { "HealthViewModel initialized" }
    }

    companion object {
        private const val DEFAULT_MAX_MONTHS = 6L
    }

    private fun setup() {
        viewModelScope.launch {
            healthRepository.observeWeightRecords(
                ZonedDateTime.now(),
                ZonedDateTime.now().minusMonths(DEFAULT_MAX_MONTHS)
            )
                .collect { result ->
                    when (result.isFailure) {
                        true -> {
                            logger.e { "Failed to observe weight records" }
                        }

                        false -> {
                            var weights = result.getOrNull() ?: emptyList()
                            weights.forEach { weight ->
                                logger.i { "Observed weight record: $weight" }
                                logger.i { weight.metadata.clientRecordId.toString() }
                            }
                            logger.i { "Observed weight records: $weights" }
                        }
                    }
                }
        }
        val weights = healthRepository.getWeights()
        _uiState.update {
            healthUiStateMapper.mapToHealthScreenState(
                weights = weights,
                selectedTimeRange = TimeRange.DAILY
            )
        }
    }

    fun onAction(action: Action) {
        when (action) {
            Action.AddWeightRecord -> {
                bottomSheetEvents.emit(BottomSheetEvents.Event.AddWeightRecord)
            }

            Action.AddBloodPressureRecord -> {
                bottomSheetEvents.emit(BottomSheetEvents.Event.AddBloodPressureRecord)
            }

            Action.HeartRateRecord -> {
                bottomSheetEvents.emit(BottomSheetEvents.Event.AddHeartRateRecord)
            }
        }
    }

    sealed interface Action {
        data object AddWeightRecord : Action
        data object AddBloodPressureRecord : Action
        data object HeartRateRecord : Action
    }
}
