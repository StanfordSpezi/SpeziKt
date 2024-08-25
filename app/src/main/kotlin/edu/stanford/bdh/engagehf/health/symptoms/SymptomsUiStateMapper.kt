package edu.stanford.bdh.engagehf.health.symptoms

import edu.stanford.bdh.engagehf.health.AggregatedHealthData
import edu.stanford.bdh.engagehf.health.HealthUiStateMapper.Companion.EPOCH_SECONDS_DIVISOR
import edu.stanford.bdh.engagehf.health.NewestHealthData
import edu.stanford.bdh.engagehf.health.TableEntryData
import edu.stanford.spezi.core.logging.SpeziLogger
import edu.stanford.spezi.core.utils.extensions.roundToDecimalPlaces
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject

class SymptomsUiStateMapper @Inject constructor() {

    private val dateTimeFormatter = DateTimeFormatter.ofPattern("MMM yyyy HH:mm")

    fun mapSymptomsUiState(
        selectedSymptomType: SymptomType,
        symptomScores: List<SymptomScore>,
    ): SymptomsUiState {
        if (symptomScores.isEmpty()) {
            return SymptomsUiState.Success(
                data = SymptomsUiData(
                    symptomScores = emptyList(),
                    chartData = emptyList(),
                    tableData = emptyList(),
                    headerData = HeaderData(
                        formattedValue = "",
                        formattedDate = "",
                        selectedSymptomType = selectedSymptomType,
                        isSelectedSymptomTypeDropdownExpanded = false
                    )
                )
            )
        }

        val symptomScoresByDay = groupScoresByDay(symptomScores)
        val chartData = calculateChartData(symptomScoresByDay, selectedSymptomType)
        val tableData = mapTableData(symptomScores, selectedSymptomType)

        val newestData = symptomScores.maxBy { it.date }
        val newestHealthData = NewestHealthData(
            formattedValue = when (selectedSymptomType) {
                SymptomType.OVERALL -> newestData.overallScore.toString()
                SymptomType.PHYSICAL_LIMITS -> newestData.physicalLimitsScore.toString()
                SymptomType.SOCIAL_LIMITS -> newestData.socialLimitsScore.toString()
                SymptomType.QUALITY_OF_LIFE -> newestData.qualityOfLifeScore.toString()
                SymptomType.SPECIFIC_SYMPTOMS -> newestData.specificSymptomsScore.toString()
                SymptomType.DIZZINESS -> newestData.dizzinessScore.toString()
            } + "%",
            formattedDate = newestData.date.format(dateTimeFormatter)
        )

        return SymptomsUiState.Success(
            data = SymptomsUiData(
                symptomScores = symptomScores,
                chartData = listOf(chartData),
                tableData = tableData,
                headerData = HeaderData(
                    formattedValue = newestHealthData.formattedValue,
                    formattedDate = newestHealthData.formattedDate,
                    selectedSymptomType = selectedSymptomType,
                    isSelectedSymptomTypeDropdownExpanded = false
                )
            )
        )
    }

    private fun mapTableData(
        symptomScores: List<SymptomScore>,
        selectedSymptomType: SymptomType,
    ): List<TableEntryData> {
        return symptomScores.mapIndexed { index, score ->
            val previousScore = if (index > 0) symptomScores[index - 1] else null
            val currentValue = getScoreForSelectedSymptomType(selectedSymptomType, score).toFloat()
            val previousValue = previousScore?.let {
                getScoreForSelectedSymptomType(selectedSymptomType, it).toFloat()
            }
            val trend = previousValue?.let { currentValue - it } ?: 0f
            val formattedTrend = if (index > 0 && previousValue != null) {
                @Suppress("MagicNumber")
                val trendPercentage = (trend / previousValue) * 100.0f
                String.format(Locale.getDefault(), PERCENT_FORMAT, trendPercentage)
            } else {
                ""
            }

            TableEntryData(
                id = null,
                value = currentValue,
                secondValue = null,
                formattedValues = "$currentValue%",
                date = score.date.toInstant().atZone(ZoneId.systemDefault()),
                formattedDate = score.date.format(dateTimeFormatter),
                trend = trend,
                formattedTrend = formattedTrend
            )
        }
    }

    private fun groupScoresByDay(symptomScores: List<SymptomScore>): Map<ZonedDateTime, List<SymptomScore>> {
        return symptomScores.groupBy { it.date }
    }

    private fun calculateChartData(
        symptomScoresByDay: Map<ZonedDateTime, List<SymptomScore>>,
        selectedSymptomType: SymptomType,
    ): AggregatedHealthData {
        val yValues = mutableListOf<Float>()
        val xValues = mutableListOf<Float>()

        symptomScoresByDay.forEach { (date, scores) ->
            val averageScore = scores.map { score ->
                getScoreForSelectedSymptomType(selectedSymptomType, score)
            }.average().toFloat()

            yValues.add(averageScore)
            val xValue = (date.toInstant().epochSecond / EPOCH_SECONDS_DIVISOR)
                .roundToDecimalPlaces(2)
            xValues.add(xValue)
        }

        SpeziLogger.i { "XValues: $xValues" }
        return AggregatedHealthData(
            yValues = yValues,
            xValues = xValues,
            seriesName = selectedSymptomType.name
        )
    }

    private fun getScoreForSelectedSymptomType(
        selectedSymptomType: SymptomType,
        score: SymptomScore,
    ) = when (selectedSymptomType) {
        SymptomType.OVERALL -> score.overallScore
        SymptomType.PHYSICAL_LIMITS -> score.physicalLimitsScore
        SymptomType.SOCIAL_LIMITS -> score.socialLimitsScore
        SymptomType.QUALITY_OF_LIFE -> score.qualityOfLifeScore
        SymptomType.SPECIFIC_SYMPTOMS -> score.specificSymptomsScore
        SymptomType.DIZZINESS -> score.dizzinessScore
    }

    companion object {
        private const val PERCENT_FORMAT = "%+.1f%%"
    }
}
