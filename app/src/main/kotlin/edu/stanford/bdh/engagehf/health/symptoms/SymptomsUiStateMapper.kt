package edu.stanford.bdh.engagehf.health.symptoms

import com.google.firebase.Timestamp
import edu.stanford.bdh.engagehf.health.AggregatedHealthData
import edu.stanford.bdh.engagehf.health.NewestHealthData
import edu.stanford.bdh.engagehf.health.TableEntryData
import edu.stanford.spezi.core.utils.LocaleProvider
import edu.stanford.spezi.core.utils.extensions.roundToDecimalPlaces
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@Suppress("MagicNumber")
class SymptomsUiStateMapper @Inject constructor(
    private val localeProvider: LocaleProvider,
) {

    private val dateTimeFormatter = DateTimeFormatter.ofPattern(MONTH_DAY_TIME_PATTERN)

    private val monthYearFormatter = DateTimeFormatter.ofPattern(MONTH_YEAR_PATTERN)

    fun mapSymptomsUiState(
        selectedSymptomType: SymptomType,
        symptomScores: List<SymptomScore>,
    ): SymptomsUiState {
        if (symptomScores.isEmpty()) {
            return SymptomsUiState.NoData("No symptom scores available")
        }

        val symptomScoresByDay = groupScoresByDay(symptomScores)
        val chartData = calculateChartData(symptomScoresByDay, selectedSymptomType)
        val tableData = mapTableData(symptomScores, selectedSymptomType)
        val newestHealthData = mapNewestHealthData(symptomScores, selectedSymptomType)

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
                ),
                valueFormatter = ::valueFormatter
            )
        )
    }

    private fun valueFormatter(value: Float): String {
        val year = value.toInt()
        val dayOfYearFraction = value - year
        val dayOfYear = (dayOfYearFraction * 365).toInt() + 1
        val date = ZonedDateTime.of(year, 1, 1, 0, 0, 0, 0, ZoneId.systemDefault())
            .plusDays((dayOfYear - 1).toLong())
        return date.format(DateTimeFormatter.ofPattern("MMM dd"))
    }

    private fun mapNewestHealthData(
        symptomScores: List<SymptomScore>,
        selectedSymptomType: SymptomType,
    ): NewestHealthData {
        val newestData = symptomScores.maxBy { it.date }
        return NewestHealthData(
            formattedValue = formatValue(newestData, selectedSymptomType),
            formattedDate = formatHeaderDate(newestData.date)
        )
    }

    private fun formatValue(newestData: SymptomScore, selectedSymptomType: SymptomType): String {
        return when (selectedSymptomType) {
            SymptomType.OVERALL -> newestData.overallScore.asPercent()
            SymptomType.PHYSICAL_LIMITS -> newestData.physicalLimitsScore.asPercent()
            SymptomType.SOCIAL_LIMITS -> newestData.socialLimitsScore.asPercent()
            SymptomType.QUALITY_OF_LIFE -> newestData.qualityOfLifeScore.asPercent()
            SymptomType.SYMPTOMS_FREQUENCY -> newestData.symptomFrequencyScore.asPercent()
            SymptomType.DIZZINESS -> newestData.dizzinessScore?.toString() ?: NOT_AVAILABLE
        }
    }

    private fun Double?.asPercent(): String = this?.let { "$it%" } ?: NOT_AVAILABLE

    private fun formatHeaderDate(timestamp: Timestamp?): String {
        val date = timestamp?.toInstant()?.atZone(ZoneId.systemDefault())
        return date?.format(monthYearFormatter) ?: ""
    }

    private fun mapTableData(
        symptomScores: List<SymptomScore>,
        selectedSymptomType: SymptomType,
    ): List<TableEntryData> {
        val sortedScores = symptomScores.sortedBy { it.date }
        return sortedScores
            .mapIndexed { index, score ->
                val previousScore =
                    sortedScores.getOrNull(index - 1)
                val currentValue =
                    getScoreForSelectedSymptomType(selectedSymptomType, score)?.toFloat()

                val previousValue = previousScore?.let {
                    getScoreForSelectedSymptomType(selectedSymptomType, it)?.toFloat()
                }

                val formattedValue = currentValue?.let {
                    if (selectedSymptomType == SymptomType.DIZZINESS) {
                        it.toString()
                    } else {
                        "$it%"
                    }
                } ?: NOT_AVAILABLE

                val trend =
                    if (previousValue != null && currentValue != null && previousValue != 0f) {
                        ((currentValue - previousValue) / previousValue) * 100
                    } else {
                        null
                    }

                val formattedTrend = trend?.let {
                    String.format(localeProvider.getDefaultLocale(), PERCENT_FORMAT, it)
                } ?: NOT_AVAILABLE

                TableEntryData(
                    id = null,
                    value = currentValue,
                    secondValue = null,
                    formattedValues = formattedValue,
                    date = score.date.toInstant().atZone(ZoneId.systemDefault()),
                    formattedDate = score.date.toInstant().atZone(ZoneId.systemDefault())
                        .format(dateTimeFormatter) ?: "",
                    trend = trend,
                    formattedTrend = formattedTrend
                )
            }.reversed()
    }

    private fun groupScoresByDay(symptomScores: List<SymptomScore>): Map<ZonedDateTime, List<SymptomScore>> {
        return symptomScores.groupBy {
            ZonedDateTime.ofInstant(
                it.date.toInstant(),
                ZoneId.systemDefault()
            )
        }
    }

    private fun calculateChartData(
        symptomScoresByDay: Map<ZonedDateTime, List<SymptomScore>>,
        selectedSymptomType: SymptomType,
    ): AggregatedHealthData {
        val yValues = mutableListOf<Float>()
        val xValues = mutableListOf<Float>()

        symptomScoresByDay.forEach { (date, scores) ->
            val filteredScores = scores.mapNotNull { score ->
                getScoreForSelectedSymptomType(selectedSymptomType, score)
            }

            if (filteredScores.isNotEmpty()) {
                val averageScore = filteredScores.average().toFloat()
                yValues.add(averageScore)
                val xValue = date.year.toFloat() + (date.dayOfYear - 1) / 365f
                xValues.add(xValue.roundToDecimalPlaces(places = 2))
            }
        }

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
        SymptomType.SYMPTOMS_FREQUENCY -> score.symptomFrequencyScore
        SymptomType.DIZZINESS -> score.dizzinessScore
    }

    companion object {
        private const val PERCENT_FORMAT = "%+.1f%%"
        private const val NOT_AVAILABLE = "N/A"
        private const val MONTH_DAY_TIME_PATTERN = "MMM dd HH:mm"
        private const val MONTH_YEAR_PATTERN = "MMM yy"
    }
}
