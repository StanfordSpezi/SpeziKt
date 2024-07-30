@file:Suppress("MagicNumber")

package edu.stanford.bdh.engagehf.health.symptoms

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.stanford.bdh.engagehf.health.TimeRange
import edu.stanford.spezi.core.logging.speziLogger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class SymptomsViewModel @Inject internal constructor() : ViewModel() {
    private val logger by speziLogger()

    private val _uiState = MutableStateFlow<SymptomsUiState>(
        SymptomsUiState.Success(
            SymptomsUiData(
                symptomScores = emptyList(),
                chartData = listOf(
                    SeriesData(
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
                        label = "Series 1",
                    ),
                    SeriesData(
                        xValues = listOf(
                            ZonedDateTime.now().toEpochSecond() / 60f,
                            ZonedDateTime.now().minusDays(27).toEpochSecond() / 60f,
                            ZonedDateTime.now().minusDays(20).toEpochSecond() / 60f,
                            ZonedDateTime.now().minusDays(12).toEpochSecond() / 60f,
                            ZonedDateTime.now().minusDays(6).toEpochSecond() / 60f
                        ),
                        yValues = listOf(20f, 30f, 40f, 50f, 60f),
                        label = "Series 2",
                    ),
                    SeriesData(
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
                        label = "Series 3",
                    ),
                    SeriesData(
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
                        label = "Series 4",
                    )
                ),
                selectedTimeRange = TimeRange.DAILY,
                headerData = HeaderData(
                    formattedValue = "90%",
                    formattedDate = ZonedDateTime.now()
                        .format(DateTimeFormatter.ofPattern("MMM dd")),
                    isSelectedTimeRangeDropdownExpanded = false
                ),
            )
        )
    )
    val uiState = _uiState.asStateFlow()

    init {
        println()
    }

    fun onAction(action: Action) {
        when (action) {
            is Action.ToggleTimeRangeDropdown -> {
                _uiState.update {
                    if (it is SymptomsUiState.Success) {
                        SymptomsUiState.Success(
                            it.data.copy(
                                headerData = it.data.headerData.copy(
                                    isSelectedTimeRangeDropdownExpanded = action.expanded
                                )
                            )
                        )
                    } else {
                        it
                    }
                }
            }

            Action.Info -> TODO()
            is Action.SelectTimeRange -> {
                _uiState.update {
                    if (it is SymptomsUiState.Success) {
                        SymptomsUiState.Success(
                            it.data.copy(
                                selectedTimeRange = action.timeRange
                            )
                        )
                    } else {
                        it
                    }
                }
            }
        }
    }

    sealed interface Action {
        data class ToggleTimeRangeDropdown(val expanded: Boolean) : Action
        data object Info : Action
        data class SelectTimeRange(val timeRange: TimeRange) : Action
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
    val symptomScores: List<SymptomScore>,
    val chartData: List<SeriesData>,
    val selectedTimeRange: TimeRange,
    val headerData: HeaderData,
)

data class HeaderData(
    val formattedValue: String,
    val formattedDate: String,
    val isSelectedTimeRangeDropdownExpanded: Boolean,
)

data class SeriesData(
    val xValues: List<Float>,
    val yValues: List<Float>,
    val label: String,
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
