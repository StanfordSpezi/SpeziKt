package edu.stanford.bdh.engagehf.bluetooth.screen

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.ComposeTestRule
import edu.stanford.spezi.core.testing.onNodeWithIdentifier

class MeasurementDialogSimulator(composeTestRule: ComposeTestRule) {
    private val root = composeTestRule.onNodeWithIdentifier(MeasurementDialogTestIdentifier.ROOT)
    private val title = composeTestRule.onNodeWithIdentifier(MeasurementDialogTestIdentifier.TITLE)
    private val weight =
        composeTestRule.onNodeWithIdentifier(MeasurementDialogTestIdentifier.WEIGHT)

    fun assertDisplayed() {
        root.assertIsDisplayed()
    }

    fun assertTitle(text: String) {
        title.assertIsDisplayed()
            .assertTextEquals(text)
    }

    fun assertWeight(text: String) {
        weight.assertIsDisplayed()
            .assertTextEquals(text)
    }
}
