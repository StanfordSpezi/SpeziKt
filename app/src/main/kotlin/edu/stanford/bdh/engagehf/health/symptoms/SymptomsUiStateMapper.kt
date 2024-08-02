package edu.stanford.bdh.engagehf.health.symptoms

import edu.stanford.bdh.engagehf.health.AggregatedHealthData
import edu.stanford.bdh.engagehf.health.NewestHealthData
import edu.stanford.bdh.engagehf.health.TableEntryData
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class SymptomsUiStateMapper @Inject constructor() {

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

        val symptomScoresByDay = symptomScores.groupBy { it.date }
        val yValues = mutableListOf<Float>()
        val xValues = mutableListOf<Float>()

        symptomScoresByDay.forEach { (date, scores) ->
            val averageScore = scores.map { score ->
                when (selectedSymptomType) {
                    SymptomType.OVERALL -> score.overallScore
                    SymptomType.PHYSICAL_LIMITS -> score.physicalLimitsScore
                    SymptomType.SOCIAL_LIMITS -> score.socialLimitsScore
                    SymptomType.QUALITY_OF_LIFE -> score.qualityOfLifeScore
                    SymptomType.SPECIFIC_SYMPTOMS -> score.specificSymptomsScore
                    SymptomType.DIZZINESS -> score.dizzinessScore
                }
            }.average().toFloat()

            yValues.add(averageScore)
            xValues.add(date.toInstant().epochSecond / 60.0f)
        }

        val chartData = AggregatedHealthData(
            yValues = yValues,
            xValues = xValues,
            seriesName = selectedSymptomType.name
        )

        val tableData = symptomScores.map { score ->
            TableEntryData(
                id = null,
                value = when (selectedSymptomType) {
                    SymptomType.OVERALL -> score.overallScore.toFloat()
                    SymptomType.PHYSICAL_LIMITS -> score.physicalLimitsScore.toFloat()
                    SymptomType.SOCIAL_LIMITS -> score.socialLimitsScore.toFloat()
                    SymptomType.QUALITY_OF_LIFE -> score.qualityOfLifeScore.toFloat()
                    SymptomType.SPECIFIC_SYMPTOMS -> score.specificSymptomsScore.toFloat()
                    SymptomType.DIZZINESS -> score.dizzinessScore.toFloat()
                },
                secondValue = null,
                formattedValues = when (selectedSymptomType) {
                    SymptomType.OVERALL -> score.overallScore.toFloat()
                    SymptomType.PHYSICAL_LIMITS -> score.physicalLimitsScore.toFloat()
                    SymptomType.SOCIAL_LIMITS -> score.socialLimitsScore.toFloat()
                    SymptomType.QUALITY_OF_LIFE -> score.qualityOfLifeScore.toFloat()
                    SymptomType.SPECIFIC_SYMPTOMS -> score.specificSymptomsScore.toFloat()
                    SymptomType.DIZZINESS -> score.dizzinessScore.toFloat()
                }.toString() + "%",
                date = score.date.toInstant().atZone(ZoneId.systemDefault()),
                formattedDate = score.date.format(DateTimeFormatter.ofPattern("MMM yyyy HH:mm")),
                trend = 0f,
                formattedTrend = ""
            )
        }

        val newestData = symptomScores.maxByOrNull { it.date }
        val newestHealthData = NewestHealthData(
            formattedValue = when (selectedSymptomType) {
                SymptomType.OVERALL -> newestData?.overallScore.toString()
                SymptomType.PHYSICAL_LIMITS -> newestData?.physicalLimitsScore.toString()
                SymptomType.SOCIAL_LIMITS -> newestData?.socialLimitsScore.toString()
                SymptomType.QUALITY_OF_LIFE -> newestData?.qualityOfLifeScore.toString()
                SymptomType.SPECIFIC_SYMPTOMS -> newestData?.specificSymptomsScore.toString()
                SymptomType.DIZZINESS -> newestData?.dizzinessScore.toString()
            } + "%",
            formattedDate = newestData?.date?.format(DateTimeFormatter.ofPattern("MMM yyyy HH:mm"))
                ?: ""
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
}
