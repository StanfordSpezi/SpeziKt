@file:Suppress("MagicNumber")

package edu.stanford.bdh.engagehf.health.symptoms

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.stanford.bdh.engagehf.health.AggregatedHealthData
import edu.stanford.bdh.engagehf.health.TableEntryData
import edu.stanford.spezi.core.logging.speziLogger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.time.ZonedDateTime
import javax.inject.Inject

@HiltViewModel
class SymptomsViewModel @Inject internal constructor(
    private val symptomsUiStateMapper: SymptomsUiStateMapper,
) : ViewModel() {
    private val logger by speziLogger()

    private val _uiState = MutableStateFlow<SymptomsUiState>(SymptomsUiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        logger.i { "SymptomsViewModel created" }
        _uiState.update {
            symptomsUiStateMapper.mapSymptomsUiState(
                selectedSymptomType = SymptomType.OVERALL,
                symptomScores = listOf(
                    SymptomScore(
                        overallScore = 80,
                        physicalLimitsScore = 70,
                        socialLimitsScore = 60,
                        qualityOfLifeScore = 50,
                        specificSymptomsScore = 40,
                        dizzinessScore = 30,
                        date = ZonedDateTime.now()
                    ),
                    SymptomScore(
                        overallScore = 70,
                        physicalLimitsScore = 60,
                        socialLimitsScore = 50,
                        qualityOfLifeScore = 40,
                        specificSymptomsScore = 30,
                        dizzinessScore = 20,
                        date = ZonedDateTime.now().minusDays(4)
                    ),
                )
            )
        }
    }

    fun onAction(action: Action) {
        when (action) {
            Action.Info -> {}
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

    data class Error(val message: String) : SymptomsUiState
}

data class SymptomsUiData(
    val symptomScores: List<SymptomScore> = emptyList(),
    val chartData: List<AggregatedHealthData>,
    val tableData: List<TableEntryData> = emptyList(),
    val headerData: HeaderData,
)

data class HeaderData(
    val formattedValue: String,
    val formattedDate: String,
    val selectedSymptomType: SymptomType,
    val isSelectedSymptomTypeDropdownExpanded: Boolean = false,
)

data class SymptomScore(
    val overallScore: Int,
    val physicalLimitsScore: Int,
    val socialLimitsScore: Int,
    val qualityOfLifeScore: Int,
    val specificSymptomsScore: Int,
    val dizzinessScore: Int,
    val date: ZonedDateTime,
)

enum class SymptomType {
    OVERALL, PHYSICAL_LIMITS, SOCIAL_LIMITS, QUALITY_OF_LIFE, SPECIFIC_SYMPTOMS, DIZZINESS
}
