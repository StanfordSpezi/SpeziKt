@file:Suppress("MagicNumber")

package edu.stanford.bdh.engagehf.health.symptoms

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.stanford.bdh.engagehf.health.AggregatedHealthData
import edu.stanford.bdh.engagehf.health.AverageHealthData
import edu.stanford.bdh.engagehf.health.NewestHealthData
import edu.stanford.bdh.engagehf.health.TableEntryData
import edu.stanford.spezi.core.logging.speziLogger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class SymptomsViewModel @Inject internal constructor(
    private val symptomsUiStateMapper: SymptomsUiStateMapper,
) : ViewModel() {
    private val logger by speziLogger()

    private val _uiState = MutableStateFlow<SymptomsUiState>(SymptomsUiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        println()
        _uiState.update {
            SymptomsUiState.Success(
                SymptomsUiData(
                    symptomScores = emptyList(), chartData = listOf(
                        AggregatedHealthData(
                            xValues = listOf(
                                ZonedDateTime.now().toEpochSecond() / 60f,
                                ZonedDateTime.now().minusDays(27).toEpochSecond() / 60f,
                                ZonedDateTime.now().minusDays(20).toEpochSecond() / 60f,
                                ZonedDateTime.now().minusDays(12).toEpochSecond() / 60f,
                                ZonedDateTime.now().minusDays(6).toEpochSecond() / 60f
                            ),
                            yValues = listOf(
                                80f, 70f, 60f, 50f, 40f
                            ),
                            seriesName = "Series 1",
                        ), AggregatedHealthData(
                            xValues = listOf(
                                ZonedDateTime.now().toEpochSecond() / 60f,
                                ZonedDateTime.now().minusDays(27).toEpochSecond() / 60f,
                                ZonedDateTime.now().minusDays(20).toEpochSecond() / 60f,
                                ZonedDateTime.now().minusDays(12).toEpochSecond() / 60f,
                                ZonedDateTime.now().minusDays(6).toEpochSecond() / 60f
                            ),
                            yValues = listOf(20f, 30f, 40f, 50f, 60f),
                            seriesName = "Series 2",
                        ), AggregatedHealthData(
                            xValues = listOf(
                                ZonedDateTime.now().toEpochSecond() / 60f,
                                ZonedDateTime.now().minusDays(27).toEpochSecond() / 60f,
                                ZonedDateTime.now().minusDays(20).toEpochSecond() / 60f,
                                ZonedDateTime.now().minusDays(12).toEpochSecond() / 60f,
                                ZonedDateTime.now().minusDays(6).toEpochSecond() / 60f
                            ),
                            yValues = listOf(
                                50f, 60f, 40f, 30f, 20f
                            ),
                            seriesName = "Series 3",
                        ), AggregatedHealthData(
                            xValues = listOf(
                                ZonedDateTime.now().toEpochSecond() / 60f,
                                ZonedDateTime.now().minusDays(27).toEpochSecond() / 60f,
                                ZonedDateTime.now().minusDays(20).toEpochSecond() / 60f,
                                ZonedDateTime.now().minusDays(12).toEpochSecond() / 60f,
                                ZonedDateTime.now().minusDays(6).toEpochSecond() / 60f
                            ),
                            yValues = listOf(
                                60f, 50f, 40f, 30f, 20f
                            ),
                            seriesName = "Series 4",
                        )
                    ),
                    headerData = HeaderData(
                        formattedValue = "Symptoms",
                        formattedDate = ZonedDateTime.now()
                            .format(DateTimeFormatter.ofPattern("MMM dd")),
                        selectedSymptomType = SymptomType.OVERALL
                    )
                )
            )
        }
    }

    fun onAction(action: Action) {
        when (action) {
            Action.Info -> TODO()
            is Action.SelectSymptomType -> {
                _uiState.update {
                    (it as? SymptomsUiState.Success)?.let { success ->
                        SymptomsUiState.Success(
                            success.data.copy(
                                headerData = success.data.headerData.copy(
                                    selectedSymptomType = action.symptomType
                                )
                            )
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
    val newestData: NewestHealthData? = null,
    val averageData: AverageHealthData? = null,
    val headerData: HeaderData,
) {
    val selectedSymptomType = headerData.selectedSymptomType
}

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
    val date: Date,
)

enum class SymptomType {
    OVERALL, PHYSICAL_LIMITS, SOCIAL_LIMITS, QUALITY_OF_LIFE, SPECIFIC_SYMPTOMS, DIZZINESS
}
