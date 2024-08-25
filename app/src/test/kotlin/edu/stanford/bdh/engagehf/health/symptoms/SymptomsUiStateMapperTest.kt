package edu.stanford.bdh.engagehf.health.symptoms

import com.google.common.truth.Truth.assertThat
import com.google.firebase.Timestamp
import edu.stanford.spezi.core.utils.LocaleProvider
import io.mockk.every
import io.mockk.mockk
import org.junit.Before
import org.junit.Test
import java.time.ZonedDateTime
import java.util.Locale

class SymptomsUiStateMapperTest {

    private val localeProvider: LocaleProvider = mockk()
    private val symptomsUiStateMapper: SymptomsUiStateMapper =
        SymptomsUiStateMapper(localeProvider = localeProvider)

    @Before
    fun setup() {
        every { localeProvider.getDefaultLocale() } returns Locale.US
    }

    @Test
    fun `mapSymptomsUiState with empty symptom scores returns NoData`() {
        // Given
        val symptomScores = emptyList<SymptomScore>()

        // When
        val result = symptomsUiStateMapper.mapSymptomsUiState(SymptomType.OVERALL, symptomScores)

        // Then
        assertThat(result).isInstanceOf(SymptomsUiState.NoData::class.java)
    }

    @Test
    fun `mapSymptomsUiState with non-empty symptom scores returns Success`() {
        // Given
        val symptomScores = listOf(
            createSymptomScore(day = 1),
            createSymptomScore(day = 2)
        )

        // When
        val result = symptomsUiStateMapper.mapSymptomsUiState(SymptomType.OVERALL, symptomScores)

        // Then
        assertThat(result).isInstanceOf(SymptomsUiState.Success::class.java)
        val successState = result as SymptomsUiState.Success
        assertThat(successState.data.symptomScores).isNotEmpty()
        assertThat(successState.data.chartData).isNotEmpty()
        assertThat(successState.data.tableData).isNotEmpty()
        assertThat(successState.data.headerData.formattedValue).isNotEmpty()
        assertThat(successState.data.headerData.formattedDate).isNotEmpty()
        assertThat(successState.data.valueFormatter(0f)).isNotEmpty()
    }

    @Test
    fun `mapSymptomsUiState with different symptom types returns correct values`() {
        // Given
        val symptomScores = listOf(
            createSymptomScore(day = 1, overallScore = 50.0, physicalLimitsScore = 40.0),
            createSymptomScore(day = 2, overallScore = 60.0, physicalLimitsScore = 50.0)
        )

        // When
        val resultOverall =
            symptomsUiStateMapper.mapSymptomsUiState(SymptomType.OVERALL, symptomScores)
        val resultPhysical =
            symptomsUiStateMapper.mapSymptomsUiState(SymptomType.PHYSICAL_LIMITS, symptomScores)

        // Then
        assertThat(resultOverall).isInstanceOf(SymptomsUiState.Success::class.java)
        assertThat(resultPhysical).isInstanceOf(SymptomsUiState.Success::class.java)

        val successStateOverall = resultOverall as SymptomsUiState.Success
        val successStatePhysical = resultPhysical as SymptomsUiState.Success

        assertThat(successStateOverall.data.headerData.formattedValue).isEqualTo("60.0%")
        assertThat(successStatePhysical.data.headerData.formattedValue).isEqualTo("50.0%")
    }

    @Test
    fun `mapSymptomsUiState returns correct formatted trend`() {
        // Given
        val symptomScores = listOf(
            createSymptomScore(day = 1, overallScore = 50.0),
            createSymptomScore(day = 2, overallScore = 55.0)
        )

        // When
        val resultOverall =
            symptomsUiStateMapper.mapSymptomsUiState(SymptomType.OVERALL, symptomScores)

        // Then
        assertThat(resultOverall).isInstanceOf(SymptomsUiState.Success::class.java)

        val successStateOverall = resultOverall as SymptomsUiState.Success

        assertThat(successStateOverall.data.tableData.first().formattedTrend).isEqualTo("+10.0%")
        assertThat(successStateOverall.data.tableData.last().formattedTrend).isEqualTo("N/A")
    }

    @Test
    fun `mapSymptomsUiState when different symptom types are null returns correct UIState`() {
        // Given
        val symptomScores = listOf(
            createSymptomScore(day = 1, overallScore = null, physicalLimitsScore = 40.0),
            createSymptomScore(day = 2, overallScore = null, physicalLimitsScore = null)
        )

        // When
        val resultOverall =
            symptomsUiStateMapper.mapSymptomsUiState(SymptomType.OVERALL, symptomScores)
        val resultPhysical =
            symptomsUiStateMapper.mapSymptomsUiState(SymptomType.PHYSICAL_LIMITS, symptomScores)

        // Then
        assertThat(resultOverall).isInstanceOf(SymptomsUiState.Success::class.java)
        assertThat(resultPhysical).isInstanceOf(SymptomsUiState.Success::class.java)
        val successStateOverall = resultOverall as SymptomsUiState.Success
        val successStatePhysical = resultPhysical as SymptomsUiState.Success
        assertThat(successStatePhysical.data.tableData.size).isEqualTo(2)
        assertThat(successStatePhysical.data.chartData.size).isEqualTo(1)
        assertThat(successStatePhysical.data.chartData[0].xValues.size).isEqualTo(1)
        assertThat(successStateOverall.data.tableData.size).isEqualTo(2)
        assertThat(successStateOverall.data.chartData.size).isEqualTo(1)
        assertThat(successStateOverall.data.chartData[0].xValues.size).isEqualTo(0)
    }

    private fun createSymptomScore(
        year: Int = 2024,
        month: Int = 8,
        day: Int = 1,
        overallScore: Double? = 70.0,
        physicalLimitsScore: Double? = 60.0,
        socialLimitsScore: Double? = 50.0,
        qualityOfLifeScore: Double? = 40.0,
        specificSymptomsScore: Double? = 30.0,
        dizzinessScore: Double? = 20.0,
    ): SymptomScore {
        return SymptomScore(
            overallScore = overallScore,
            physicalLimitsScore = physicalLimitsScore,
            socialLimitsScore = socialLimitsScore,
            qualityOfLifeScore = qualityOfLifeScore,
            symptomFrequencyScore = specificSymptomsScore,
            dizzinessScore = dizzinessScore,
            date = Timestamp(
                ZonedDateTime.of(
                    year,
                    month,
                    day,
                    0,
                    0,
                    0,
                    0,
                    ZonedDateTime.now().zone
                ).toInstant()
            )
        )
    }
}
