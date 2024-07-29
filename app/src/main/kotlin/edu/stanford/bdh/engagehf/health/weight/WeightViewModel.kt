package edu.stanford.bdh.engagehf.health.weight

import androidx.health.connect.client.records.WeightRecord
import androidx.health.connect.client.records.metadata.Metadata
import androidx.health.connect.client.units.Mass
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.stanford.bdh.engagehf.bluetooth.component.BottomSheetEvents
import edu.stanford.bdh.engagehf.health.HealthRepository
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
    private val uiStateMapper: WeightUiStateMapper,
    private val healthRepository: HealthRepository,
) : ViewModel() {
    private val logger by speziLogger()

    private val _uiState = MutableStateFlow<WeightUiState>(WeightUiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        setup()
        logger.i { "WeightViewModel initialized" }
    }

    companion object {
        private const val DEFAULT_MAX_MONTHS = 6L
    }

    private fun setup() {
        viewModelScope.launch {
            healthRepository.observeWeightRecords(
                ZonedDateTime.now(), ZonedDateTime.now().minusMonths(DEFAULT_MAX_MONTHS)
            ).collect { result ->
                when (result.isFailure) {
                    true -> {
                        _uiState.update {
                            WeightUiState.Error("Failed to observe weight records")
                        }
                    }

                    false -> {
                        val weights: List<WeightRecord> = listOf(
                            WeightRecord(
                                time = ZonedDateTime.now().toInstant(),
                                zoneOffset = ZonedDateTime.now().offset,
                                weight = Mass.kilograms(80.0),
                                metadata = Metadata(clientRecordId = "1")
                            ),
                            WeightRecord(
                                time = ZonedDateTime.now().minusDays(1).toInstant(),
                                zoneOffset = ZonedDateTime.now().offset,
                                weight = Mass.kilograms(79.0),
                                metadata = Metadata(clientRecordId = "1")
                            ),
                            WeightRecord(
                                time = ZonedDateTime.now().minusDays(5).toInstant(),
                                zoneOffset = ZonedDateTime.now().offset,
                                weight = Mass.kilograms(78.0),
                                metadata = Metadata(clientRecordId = "1")
                            ),
                            WeightRecord(
                                time = ZonedDateTime.now().minusDays(10).toInstant(),
                                zoneOffset = ZonedDateTime.now().offset,
                                weight = Mass.kilograms(77.0),
                                metadata = Metadata(clientRecordId = "1")
                            ),
                            WeightRecord(
                                time = ZonedDateTime.now().minusDays(15).toInstant(),
                                zoneOffset = ZonedDateTime.now().offset,
                                weight = Mass.kilograms(76.0),
                                metadata = Metadata(clientRecordId = "1")
                            ),
                            WeightRecord(
                                time = ZonedDateTime.now().minusDays(20).toInstant(),
                                zoneOffset = ZonedDateTime.now().offset,
                                weight = Mass.kilograms(75.0),
                                metadata = Metadata(clientRecordId = "1")
                            ),
                            WeightRecord(
                                time = ZonedDateTime.now().minusDays(25).toInstant(),
                                zoneOffset = ZonedDateTime.now().offset,
                                weight = Mass.kilograms(74.0),
                                metadata = Metadata(clientRecordId = "1")
                            ),
                            WeightRecord(
                                time = ZonedDateTime.now().minusDays(30).toInstant(),
                                zoneOffset = ZonedDateTime.now().offset,
                                weight = Mass.kilograms(73.0),
                                metadata = Metadata(clientRecordId = "1")
                            ),
                            WeightRecord(
                                time = ZonedDateTime.now().minusDays(35).toInstant(),
                                zoneOffset = ZonedDateTime.now().offset,
                                weight = Mass.kilograms(72.0),
                                metadata = Metadata(clientRecordId = "1")
                            ),
                            WeightRecord(
                                time = ZonedDateTime.now().minusDays(40).toInstant(),
                                zoneOffset = ZonedDateTime.now().offset,
                                weight = Mass.kilograms(71.0),
                                metadata = Metadata(clientRecordId = "1")
                            ),
                            WeightRecord(
                                time = ZonedDateTime.now().minusDays(45).toInstant(),
                                zoneOffset = ZonedDateTime.now().offset,
                                weight = Mass.kilograms(70.0),
                                metadata = Metadata(clientRecordId = "1")
                            ),
                            )
                        _uiState.update {
                            WeightUiState.Success(
                                uiStateMapper.mapToWeightUiState(
                                    records = weights,
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
        when (action) {
            Action.AddWeightRecord -> {
                bottomSheetEvents.emit(BottomSheetEvents.Event.AddWeightRecord)
            }

            is Action.DeleteWeightRecord -> {
                healthRepository.deleteWeight(action.weightId)
                logger.i { "WeightViewModel.onAction Delete Weight Record: ${action.weightId}" }
            }

            is Action.UpdateTimeRange -> {
                when (val uiState = _uiState.value) {
                    is WeightUiState.Loading -> return
                    is WeightUiState.Error -> return
                    is WeightUiState.Success -> {
                        _uiState.update {
                            WeightUiState.Success(
                                uiStateMapper.mapToWeightUiState(
                                    uiState.data.weights,
                                    selectedTimeRange = action.timeRange
                                )
                            )
                        }
                    }
                }
            }

            Action.WeightDescriptionBottomSheet -> {
                bottomSheetEvents.emit(BottomSheetEvents.Event.WeightDescriptionBottomSheet)
            }

            is Action.ToggleTimeRangeDropdown -> {
                _uiState.update {
                    when (val uiState = _uiState.value) {
                        is WeightUiState.Loading -> uiState
                        is WeightUiState.Error -> uiState
                        is WeightUiState.Success -> {
                            uiState.copy(
                                data = uiState.data.copy(
                                    isSelectedTimeRangeDropdownExpanded = action.expanded
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    sealed interface Action {
        data object WeightDescriptionBottomSheet : Action
        data object AddWeightRecord : Action
        data class DeleteWeightRecord(val weightId: String) : Action
        data class UpdateTimeRange(val timeRange: TimeRange) : Action
        data class ToggleTimeRangeDropdown(val expanded: Boolean) : Action
    }
}
