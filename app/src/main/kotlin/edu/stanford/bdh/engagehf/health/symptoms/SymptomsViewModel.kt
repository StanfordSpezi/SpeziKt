@file:Suppress("MagicNumber")

package edu.stanford.bdh.engagehf.health.symptoms

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.stanford.bdh.engagehf.R
import edu.stanford.bdh.engagehf.bluetooth.component.AppScreenEvents
import edu.stanford.bdh.engagehf.health.AggregatedHealthData
import edu.stanford.bdh.engagehf.health.HealthRepository
import edu.stanford.bdh.engagehf.health.TableEntryData
import edu.stanford.spezi.core.logging.speziLogger
import edu.stanford.spezi.ui.StringResource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class SymptomsViewModel @Inject internal constructor(
    private val symptomsUiStateMapper: SymptomsUiStateMapper,
    private val healthRepository: HealthRepository,
    private val appScreenEvents: AppScreenEvents,
) : ViewModel() {
    private val logger by speziLogger()

    private val _uiState = MutableStateFlow<SymptomsUiState>(SymptomsUiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        logger.i { "SymptomsViewModel created" }
        setup()
    }

    private fun setup() {
        viewModelScope.launch {
            healthRepository.observeSymptoms().collect { result ->
                result.onFailure {
                    _uiState.update {
                        SymptomsUiState.Error(StringResource(R.string.failed_to_observe_symptom_scores))
                    }
                }.onSuccess { successResult ->
                    _uiState.update {
                        logger.i { "Symptoms data received: $successResult" }
                        symptomsUiStateMapper.mapSymptomsUiState(
                            selectedSymptomType = SymptomType.OVERALL,
                            symptomScores = successResult
                        )
                    }
                }
            }
        }
    }

    fun onAction(action: Action) {
        when (action) {
            is Action.Info -> {
                appScreenEvents.emit(
                    AppScreenEvents.Event.SymptomsDescriptionBottomSheet
                )
            }

            is Action.SelectSymptomType -> {
                _uiState.update {
                    (it as? SymptomsUiState.Success)?.let { success ->
                        symptomsUiStateMapper.mapSymptomsUiState(
                            selectedSymptomType = action.symptomType,
                            symptomScores = success.data.symptomScores
                        )
                    } ?: it
                }
            }

            is Action.ToggleSymptomTypeDropdown -> {
                _uiState.update {
                    (it as? SymptomsUiState.Success)?.let { success ->
                        SymptomsUiState.Success(
                            success.data.copy(
                                headerData = success.data.headerData.copy(
                                    isSelectedSymptomTypeDropdownExpanded = action.isExpanded
                                )
                            )
                        )
                    } ?: it
                }
            }
        }
    }

    sealed interface Action {
        data object Info : Action
        data class SelectSymptomType(val symptomType: SymptomType) : Action
        data class ToggleSymptomTypeDropdown(val isExpanded: Boolean) : Action
    }
}

sealed interface SymptomsUiState {
    data object Loading : SymptomsUiState
    data class Success(
        val data: SymptomsUiData,
    ) : SymptomsUiState

    data class NoData(val message: StringResource) : SymptomsUiState
    data class Error(val message: StringResource) : SymptomsUiState
}

data class SymptomsUiData(
    val symptomScores: List<SymptomScore> = emptyList(),
    val chartData: List<AggregatedHealthData>,
    val tableData: List<TableEntryData> = emptyList(),
    val headerData: HeaderData,
    val xValueFormatter: (Double) -> String = { "" },
)

data class HeaderData(
    val formattedValue: String,
    val formattedDate: String,
    val selectedSymptomType: SymptomType,
    val isSelectedSymptomTypeDropdownExpanded: Boolean = false,
)

data class SymptomScore(
    val overallScore: Double? = null,
    val physicalLimitsScore: Double? = null,
    val socialLimitsScore: Double? = null,
    val qualityOfLifeScore: Double? = null,
    val symptomFrequencyScore: Double? = null,
    val dizzinessScore: Double? = null,
    val date: String? = null,
    val formattedDate: String = "",
) {
    val zonedDateTime: ZonedDateTime by lazy {
        runCatching {
            OffsetDateTime
                .parse(date, DateTimeFormatter.ISO_DATE_TIME)
                .toInstant()
                .atZone(ZoneId.systemDefault())
        }.getOrDefault(ZonedDateTime.now())
    }
}

enum class SymptomType {
    OVERALL, PHYSICAL_LIMITS, SOCIAL_LIMITS, QUALITY_OF_LIFE, SPECIFIC, DIZZINESS
}
