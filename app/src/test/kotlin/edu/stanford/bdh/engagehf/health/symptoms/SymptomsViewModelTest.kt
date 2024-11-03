package edu.stanford.bdh.engagehf.health.symptoms

import com.google.common.truth.Truth.assertThat
import edu.stanford.bdh.engagehf.R
import edu.stanford.bdh.engagehf.bluetooth.component.AppScreenEvents
import edu.stanford.bdh.engagehf.health.HealthRepository
import edu.stanford.spezi.core.design.component.StringResource
import edu.stanford.spezi.core.testing.CoroutineTestRule
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.random.Random

class SymptomsViewModelTest {

    @get:Rule
    val coroutineTestRule = CoroutineTestRule()

    private val healthRepository: HealthRepository = mockk()
    private val symptomsUiStateMapper: SymptomsUiStateMapper = mockk()
    private val symptomScores = getSymptomScores()
    private val successState = SymptomsUiState.Success(getSymptomsUiData())
    private val appScreenEvents: AppScreenEvents = mockk(relaxed = true)

    private lateinit var viewModel: SymptomsViewModel

    @Before
    fun setup() {
        coEvery { healthRepository.observeSymptoms() } returns flowOf(Result.success(symptomScores))
        every {
            symptomsUiStateMapper.mapSymptomsUiState(
                selectedSymptomType = SymptomType.OVERALL,
                symptomScores = symptomScores,
            )
        } returns successState
    }

    @Test
    fun `given symptom scores when initialized then uiState is success`() {
        // given
        createViewModel()

        // when
        val uiState = viewModel.uiState.value

        // then
        assertThat(uiState).isEqualTo(successState)
    }

    @Test
    fun `it should indicate error state in case of failure of observing symptoms`() {
        // given
        coEvery {
            healthRepository.observeSymptoms()
        } returns flowOf(Result.failure(Error("Error")))

        // when
        createViewModel()

        // then
        assertThat(viewModel.uiState.value)
            .isEqualTo(SymptomsUiState.Error("Failed to observe symptom scores"))
    }

    @Test
    fun `it should handle ToggleSymptomTypeDropdown action correctly`() {
        // given
        createViewModel()
        val isExpanded = Random.nextBoolean()

        // when
        viewModel.onAction(SymptomsViewModel.Action.ToggleSymptomTypeDropdown(isExpanded))

        // then
        val uiState = viewModel.uiState.value as SymptomsUiState.Success
        assertThat(uiState.data.headerData.isSelectedSymptomTypeDropdownExpanded).isEqualTo(
            isExpanded
        )
    }

    @Test
    fun `given symptom scores when SelectSymptomType action is triggered then uiState is updated`() {
        // given
        createViewModel()
        val newSymptomType = SymptomType.PHYSICAL_LIMITS
        val newState: SymptomsUiState.Success = mockk()
        every {
            symptomsUiStateMapper.mapSymptomsUiState(
                selectedSymptomType = newSymptomType,
                symptomScores = successState.data.symptomScores,
            )
        } returns newState

        // when
        viewModel.onAction(SymptomsViewModel.Action.SelectSymptomType(newSymptomType))

        // then
        val uiState = viewModel.uiState.value
        assertThat(uiState).isEqualTo(newState)
    }

    @Test
    fun `it should handle SymptomsDescriptionBottomSheet correctly`() {
        // given
        createViewModel()

        // when
        viewModel.onAction(SymptomsViewModel.Action.Info)

        // then
        verify { appScreenEvents.emit(AppScreenEvents.Event.SymptomsDescriptionBottomSheet) }
    }

    private fun getSymptomScores() = listOf(
        SymptomScore(
            overallScore = 80.0,
            physicalLimitsScore = 70.0,
            socialLimitsScore = 60.0,
            qualityOfLifeScore = 90.0,
            symptomFrequencyScore = 50.0,
            dizzinessScore = 40.0,
            date = null
        )
    )

    private fun getSymptomsUiData() = SymptomsUiData(
        chartData = emptyList(),
        headerData = HeaderData(
            formattedDate = "",
            formattedValue = "",
            selectedSymptomType = SymptomType.SYMPTOMS_FREQUENCY,
            selectedSymptomTypeText = StringResource(R.string.symptom_type_overall),
        )
    )

    private fun createViewModel() {
        viewModel = SymptomsViewModel(
            symptomsUiStateMapper = symptomsUiStateMapper,
            healthRepository = healthRepository,
            appScreenEvents = appScreenEvents,
        )
    }
}
