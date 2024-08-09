package edu.stanford.bdh.engagehf.medication

import androidx.compose.ui.test.junit4.createComposeRule
import edu.stanford.bdh.engagehf.simulator.MedicationScreenSimulator
import org.junit.Rule
import org.junit.Test

class MedicationScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `it should display the success correctly`() {
        val medicationDetailsList = getMedicationDetailsList()
        val medicationDetails = medicationDetailsList.first()
        setUiState(MedicationUiState.Success(medicationDetailsList))
        medicationScreen {
            assertSuccessIsDisplayed()
        }
    }

    @Test
    fun `it should display the loading correctly`() {
        setUiState(MedicationUiState.Loading)
        medicationScreen {
            assertLoadingIsDisplayed()
        }
    }

    @Test
    fun `it should display the error correctly`() {
        val errorMessage = "An error occurred"
        setUiState(MedicationUiState.Error(errorMessage))
        medicationScreen {
            assertErrorIsDisplayed()
            assertErrorTextIsDisplayed(errorMessage)
        }
    }

    private fun setUiState(uiState: MedicationUiState) {
        composeTestRule.setContent {
            MedicationScreen(uiState = uiState, onAction = {})
        }
    }

    private fun medicationScreen(block: MedicationScreenSimulator.() -> Unit) {
        MedicationScreenSimulator(composeTestRule).apply(block)
    }

    private fun getMedicationDetailsList() = listOf(
        MedicationDetails(
            id = "123",
            title = "Medication",
            subtitle = "Subtitle 1",
            dosageInformation = DosageInformation(
                currentSchedule = listOf(DoseSchedule(2.0, listOf(20.0))),
                minimumSchedule = listOf(DoseSchedule(1.0, listOf(10.0))),
                targetSchedule = listOf(DoseSchedule(2.0, listOf(20.0))),
                unit = "mg",
            ),
            description = "Description 1",
            type = MedicationRecommendationType.TARGET_DOSE_REACHED,
            isExpanded = true
        ),
    )
}
