package edu.stanford.bdh.engagehf.health.symptoms

import com.google.common.truth.Truth.assertThat
import com.google.firebase.Timestamp
import edu.stanford.bdh.engagehf.health.HealthRepository
import edu.stanford.spezi.core.testing.CoroutineTestRule
import edu.stanford.spezi.core.testing.runTestUnconfined
import edu.stanford.spezi.core.utils.LocaleProvider
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.Locale

class SymptomsViewModelTest {

    @get:Rule
    val coroutineTestRule = CoroutineTestRule()

    private val localeProvider: LocaleProvider = mockk()
    private val healthRepository: HealthRepository = mockk()
    private val symptomsUiStateMapper: SymptomsUiStateMapper = SymptomsUiStateMapper(localeProvider)
    private val symptomScores = getSymptomScores()

    private lateinit var viewModel: SymptomsViewModel

    @Before
    fun setup() {
        every { localeProvider.getDefaultLocale() } returns Locale.US
        coEvery { healthRepository.observeSymptoms() } returns flowOf(Result.success(symptomScores))
        viewModel = SymptomsViewModel(symptomsUiStateMapper, healthRepository)
    }

    @Test
    fun `given symptom scores when initialized then uiState is success`() = runTestUnconfined {
        // when
        val uiState = viewModel.uiState.value

        // then
        assertThat(uiState).isInstanceOf(SymptomsUiState.Success::class.java)
    }

    @Test
    fun `given symptom scores when ToggleSymptomTypeDropdown action is triggered then uiState is updated`() =
        runTestUnconfined {
            // given
            val isExpanded = true

            // when
            viewModel.onAction(SymptomsViewModel.Action.ToggleSymptomTypeDropdown(isExpanded))

            // then
            val uiState = viewModel.uiState.value
            assertThat(uiState).isInstanceOf(SymptomsUiState.Success::class.java)
            assertThat((uiState as SymptomsUiState.Success).data.headerData.isSelectedSymptomTypeDropdownExpanded).isEqualTo(
                isExpanded
            )
        }

    @Test
    fun `given symptom scores when SelectSymptomType action is triggered then uiState is updated`() =
        runTestUnconfined {
            // given
            val newSymptomType = SymptomType.PHYSICAL_LIMITS

            // when
            viewModel.onAction(SymptomsViewModel.Action.SelectSymptomType(newSymptomType))

            // then
            val uiState = viewModel.uiState.value
            assertThat(uiState).isInstanceOf(SymptomsUiState.Success::class.java)
            assertThat((uiState as SymptomsUiState.Success).data.headerData.selectedSymptomType).isEqualTo(
                newSymptomType
            )
        }

    private fun getSymptomScores() = listOf(
        SymptomScore(
            overallScore = 80.0,
            physicalLimitsScore = 70.0,
            socialLimitsScore = 60.0,
            qualityOfLifeScore = 90.0,
            symptomFrequencyScore = 50.0,
            dizzinessScore = 40.0,
            date = Timestamp.now()
        )
    )
}
