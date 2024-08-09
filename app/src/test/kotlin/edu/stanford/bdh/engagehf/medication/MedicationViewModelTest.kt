package edu.stanford.bdh.engagehf.medication

import com.google.common.truth.Truth.assertThat
import edu.stanford.spezi.core.testing.CoroutineTestRule
import edu.stanford.spezi.core.testing.runTestUnconfined
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class MedicationViewModelTest {

    @get:Rule
    val coroutineTestRule = CoroutineTestRule()

    private val medicationRepository: MedicationRepository = mockk()
    private var medicationUiStateMapper: MedicationUiStateMapper = MedicationUiStateMapper()

    private lateinit var viewModel: MedicationViewModel

    @Before
    fun setup() {
        coEvery { medicationRepository.observeMedicationDetails() } returns flowOf(
            Result.success(
                getMedicationDetailsList()
            )
        )
        viewModel = MedicationViewModel(medicationRepository, medicationUiStateMapper)
    }

    @Test
    fun `given medication details when initialized then uiState is success`() = runTestUnconfined {
        // given
        val medicationDetails = getMedicationDetailsList()

        // then
        val uiState = viewModel.uiState.value
        assertThat(uiState).isInstanceOf(MedicationUiState.Success::class.java)
        assertThat((uiState as MedicationUiState.Success).medicationDetails).isEqualTo(
            medicationDetails
        )
    }

    @Test
    fun `given success state when expand action then uiState is updated`() = runTestUnconfined {
        // when
        viewModel.onAction(MedicationViewModel.Action.ExpandMedication("1", true))

        // then
        val result = viewModel.uiState.value
        assertThat(result).isInstanceOf(MedicationUiState.Success::class.java)
        assertThat((result as MedicationUiState.Success).medicationDetails[0].isExpanded).isTrue()
    }

    private fun getMedicationDetailsList() = listOf(
        MedicationDetails(
            id = "1",
            title = "Medication A",
            subtitle = "Subtitle A",
            description = "Description A",
            type = MedicationRecommendationType.TARGET_DOSE_REACHED,
            dosageInformation = null,
            isExpanded = false
        ),
        MedicationDetails(
            id = "2",
            title = "Medication B",
            subtitle = "Subtitle B",
            description = "Description B",
            type = MedicationRecommendationType.NOT_STARTED,
            dosageInformation = null,
            isExpanded = false
        )
    )
}
