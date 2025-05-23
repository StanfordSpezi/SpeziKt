package edu.stanford.bdh.engagehf.medication.ui

import androidx.compose.ui.test.junit4.createComposeRule
import edu.stanford.bdh.engagehf.simulator.MedicationScreenSimulator
import edu.stanford.spezi.modules.design.R
import edu.stanford.spezi.ui.StringResource
import org.junit.Rule
import org.junit.Test

class MedicationScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `it should display the success correctly`() {
        setUiState(
            MedicationUiState.Success(
                medicationsTaking = Medications(
                    medications = getMedicationCardUiModels(),
                    expanded = true
                ),
                medicationsThatMayHelp = Medications(
                    medications = getMedicationCardUiModels(),
                    expanded = true
                ),
                colorKeyExpanded = true,
            )
        )
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
        setUiState(MedicationUiState.Error(StringResource(errorMessage)))
        medicationScreen {
            assertErrorTextIsDisplayed(errorMessage)
            assertCenteredContent()
        }
    }

    @Test
    fun `it should display the no data message correctly`() {
        val message = "No medication recommendations"
        setUiState(MedicationUiState.NoData(StringResource(message)))
        medicationScreen {
            assertNoDataTextIsDisplayed(message)
            assertCenteredContent()
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

    private fun getMedicationCardUiModels() = listOf(
        MedicationCardUiModel(
            id = "1",
            title = "Medication 1",
            subtitle = "Subtitle 1",
            description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. " +
                "Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.",
            dosageInformation = DosageInformationUiModel(
                currentDose = DosageRowInfoData(
                    label = "Current Dose:",
                    dosageValues = listOf(
                        "1.0 mg daily",
                        "2.0 mg daily",
                    )
                ),
                targetDose = DosageRowInfoData(
                    label = "Target Dose:",
                    dosageValues = listOf(
                        "1.0 mg daily",
                    )
                ),
                progress = 0.234f,
            ),
            isExpanded = true,
            statusColor = MedicationColor.BLUE,
            statusIconResId = R.drawable.ic_check,
            videoPath = "/videoSections/1/videos/1"
        )
    )
}
