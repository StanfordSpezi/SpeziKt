package edu.stanford.bdh.engagehf.simulator

import androidx.compose.ui.test.assertAny
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.ComposeTestRule
import edu.stanford.bdh.engagehf.bluetooth.screen.MeasurementDialogTestIdentifier
import edu.stanford.spezi.testing.ui.onAllNodes
import edu.stanford.spezi.testing.ui.onNodeWithIdentifier

class MeasurementDialogSimulator(composeTestRule: ComposeTestRule) {
    private val root = composeTestRule.onNodeWithIdentifier(MeasurementDialogTestIdentifier.ROOT)
    private val title = composeTestRule.onNodeWithIdentifier(MeasurementDialogTestIdentifier.TITLE)
    private val measurementLabel =
        composeTestRule.onAllNodes(MeasurementDialogTestIdentifier.MEASUREMENT_LABEL)
    private val measurementValue =
        composeTestRule.onAllNodes(MeasurementDialogTestIdentifier.MEASUREMENT_VALUE)

    fun assertDisplayed() {
        root.assertIsDisplayed()
    }

    fun assertTitle(text: String) {
        title.assertIsDisplayed().assertTextEquals(text)
    }

    fun assertLabel(text: String) {
        measurementLabel.assertAny(hasText(text))
    }

    fun assertValue(text: String) {
        measurementValue.assertAny(hasText(text))
    }
}
