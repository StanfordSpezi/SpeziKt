package edu.stanford.bdh.engagehf.simulator

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.ComposeTestRule
import edu.stanford.bdh.engagehf.medication.ui.MedicationScreenTestIdentifier
import edu.stanford.spezi.testing.ui.onNodeWithIdentifier
import edu.stanford.spezi.ui.CenteredBoxContentTestIdentifier

class MedicationScreenSimulator(
    composeTestRule: ComposeTestRule,
) {
    private val success =
        composeTestRule.onNodeWithIdentifier(MedicationScreenTestIdentifier.SUCCESS)

    private val loading =
        composeTestRule.onNodeWithIdentifier(MedicationScreenTestIdentifier.LOADING)

    private val centeredContent =
        composeTestRule.onNodeWithIdentifier(CenteredBoxContentTestIdentifier.ROOT)

    private val errorMessage =
        composeTestRule.onNodeWithIdentifier(MedicationScreenTestIdentifier.ERROR_TEXT)

    private val noDataMessage =
        composeTestRule.onNodeWithIdentifier(MedicationScreenTestIdentifier.NO_DATA_TEXT)

    fun assertSuccessIsDisplayed() {
        success.assertIsDisplayed()
    }

    fun assertLoadingIsDisplayed() {
        loading.assertIsDisplayed()
    }

    fun assertErrorTextIsDisplayed(errorText: String) {
        errorMessage
            .assertIsDisplayed()
            .assertTextEquals(errorText)
    }

    fun assertNoDataTextIsDisplayed(message: String) {
        noDataMessage
            .assertIsDisplayed()
            .assertTextEquals(message)
    }

    fun assertCenteredContent() {
        centeredContent.assertIsDisplayed()
    }
}
