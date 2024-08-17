package edu.stanford.bdh.engagehf.medication.ui

import com.google.common.truth.Truth.assertThat
import edu.stanford.bdh.engagehf.medication.data.MedicationRecommendation
import edu.stanford.bdh.engagehf.medication.data.MedicationRecommendationType
import edu.stanford.bdh.engagehf.medication.data.MedicationRepository
import edu.stanford.spezi.core.testing.CoroutineTestRule
import edu.stanford.spezi.core.testing.runTestUnconfined
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class MedicationViewModelTest {

    @get:Rule
    val coroutineTestRule = CoroutineTestRule()

    private val medicationRepository: MedicationRepository = mockk()
    private val medicationUiStateMapper: MedicationUiStateMapper = mockk()
    private val recommendations = getMedicationRecommendations()
    private val uiModels: List<MedicationCardUiModel> = mockk()

    private lateinit var viewModel: MedicationViewModel

    @Before
    fun setup() {
        coEvery { medicationRepository.observeMedicationRecommendations() } returns flowOf(
            Result.success(recommendations)
        )
        every {
            medicationUiStateMapper.mapMedicationUiState(recommendations)
        } returns MedicationUiState.Success(uiModels)
        viewModel = MedicationViewModel(medicationRepository, medicationUiStateMapper)
    }

    @Test
    fun `given medication details when initialized then uiState is success`() = runTestUnconfined {
        // given
        every {
            medicationUiStateMapper.mapMedicationUiState(recommendations)
        } returns MedicationUiState.Success(uiModels)

        // when
        val uiState = viewModel.uiState.value

        // then
        assertThat(uiState).isInstanceOf(MedicationUiState.Success::class.java)
        assertThat((uiState as MedicationUiState.Success).uiModels).isEqualTo(uiModels)
    }

    @Test
    fun `given success state when expand action then uiState is updated`() = runTestUnconfined {
        // given
        val medicationId = "some-id"
        val toggledResult = MedicationUiState.Success(uiModels = emptyList())
        every {
            medicationUiStateMapper.expandMedication(
                medicationId = medicationId,
                uiState = any()
            )
        } returns toggledResult

        // when
        viewModel.onAction(MedicationViewModel.Action.ToggleExpand(medicationId))

        // then
        val result = viewModel.uiState.value
        assertThat(result).isEqualTo(toggledResult)
    }

    private fun getMedicationRecommendations() = listOf(
        MedicationRecommendation(
            id = "1",
            title = "Medication A",
            subtitle = "Subtitle A",
            description = "Description A",
            type = MedicationRecommendationType.TARGET_DOSE_REACHED,
            dosageInformation = null,
        ),
        MedicationRecommendation(
            id = "2",
            title = "Medication B",
            subtitle = "Subtitle B",
            description = "Description B",
            type = MedicationRecommendationType.NOT_STARTED,
            dosageInformation = null,
        )
    )
}