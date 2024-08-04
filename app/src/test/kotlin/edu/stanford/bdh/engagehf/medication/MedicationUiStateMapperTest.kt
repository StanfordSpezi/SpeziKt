package edu.stanford.bdh.engagehf.medication

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class MedicationUiStateMapperTest {

    private var medicationUiStateMapper: MedicationUiStateMapper = MedicationUiStateMapper()

    @Test
    fun `given medication details when mapMedicationUiState then return sorted success state`() {
        // given
        val medicationDetails = getMedicationDetailsList()

        // when
        val result = medicationUiStateMapper.mapMedicationUiState(medicationDetails)

        // then
        assertThat(result).isInstanceOf(MedicationUiState.Success::class.java)
        assertThat((result as MedicationUiState.Success).medicationDetails).hasSize(2)
        assertThat(result.medicationDetails[0].id).isEqualTo("1")
        assertThat(result.medicationDetails[1].id).isEqualTo("2")
    }

    @Test
    fun `given error state and expand action when expandMedication then return error state`() {
        // given
        val uiState = MedicationUiState.Error(message = "An error occurred")
        val action =
            MedicationViewModel.Action.ExpandMedication(medicationId = "1", isExpanded = true)

        // when
        val result = medicationUiStateMapper.expandMedication(action, uiState)

        // then
        assertThat(result).isEqualTo(uiState)
    }

    @Test
    fun `given success state and expand action when expandMedication then return updated success state`() {
        // given
        val uiState = MedicationUiState.Success(
            medicationDetails = getMedicationDetailsList()
        )
        val action =
            MedicationViewModel.Action.ExpandMedication(medicationId = "1", isExpanded = true)

        // when
        val result = medicationUiStateMapper.expandMedication(action, uiState)

        // then
        assertThat(result).isInstanceOf(MedicationUiState.Success::class.java)
        assertThat((result as MedicationUiState.Success).medicationDetails).hasSize(2)
        assertThat(result.medicationDetails[0].isExpanded).isFalse()
        assertThat(result.medicationDetails[1].isExpanded).isTrue()
    }

    private fun getMedicationDetailsList() = listOf(
        MedicationDetails(
            id = "2",
            title = "Medication B",
            subtitle = "Subtitle B",
            description = "Description B",
            type = MedicationRecommendationType.NOT_STARTED,
            dosageInformation = null
        ),
        MedicationDetails(
            id = "1",
            title = "Medication A",
            subtitle = "Subtitle A",
            description = "Description A",
            type = MedicationRecommendationType.TARGET_DOSE_REACHED, // higher priority than NOT_STARTED
            dosageInformation = null
        )
    )
}
