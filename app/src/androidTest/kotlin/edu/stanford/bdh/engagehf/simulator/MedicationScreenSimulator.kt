package edu.stanford.bdh.engagehf.simulator

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.ComposeTestRule
import edu.stanford.bdh.engagehf.medication.MedicationScreenTestIdentifier
import edu.stanford.spezi.core.testing.onNodeWithIdentifier

class MedicationScreenSimulator(
    private val composeTestRule: ComposeTestRule,
) {
    private val success =
        composeTestRule.onNodeWithIdentifier(MedicationScreenTestIdentifier.SUCCESS)

    private val loading =
        composeTestRule.onNodeWithIdentifier(MedicationScreenTestIdentifier.LOADING)

    private val errorRoot =
        composeTestRule.onNodeWithIdentifier(MedicationScreenTestIdentifier.ERROR_ROOT)

    private val errorMessage =
        composeTestRule.onNodeWithIdentifier(MedicationScreenTestIdentifier.ERROR_TEXT)

    fun assertSuccessIsDisplayed() {
        success.assertIsDisplayed()
    }

    fun assertLoadingIsDisplayed() {
        loading.assertIsDisplayed()
    }

    fun assertErrorIsDisplayed() {
        errorRoot.assertIsDisplayed()
    }

    fun assertErrorTextIsDisplayed(errorText: String) {
        errorMessage
            .assertIsDisplayed()
            .assertTextEquals(errorText)
    }
}
