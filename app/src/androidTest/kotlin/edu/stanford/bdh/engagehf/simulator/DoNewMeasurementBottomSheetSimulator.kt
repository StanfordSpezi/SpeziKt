package edu.stanford.bdh.engagehf.simulator

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.ComposeTestRule
import edu.stanford.bdh.engagehf.bluetooth.component.DoNewMeasurementBottomSheetTestIdentifier
import edu.stanford.spezi.ui.testing.onNodeWithIdentifier

class DoNewMeasurementBottomSheetSimulator(
    composeTestRule: ComposeTestRule,
) {
    private val title =
        composeTestRule.onNodeWithIdentifier(DoNewMeasurementBottomSheetTestIdentifier.TITLE)

    private val progressBar =
        composeTestRule.onNodeWithIdentifier(DoNewMeasurementBottomSheetTestIdentifier.PROGRESS_BAR)

    private val description =
        composeTestRule.onNodeWithIdentifier(DoNewMeasurementBottomSheetTestIdentifier.DESCRIPTION)

    private val bloodPressureIcon =
        composeTestRule.onNodeWithIdentifier(DoNewMeasurementBottomSheetTestIdentifier.BLOOD_PRESSURE_ICON)

    private val weightIcon =
        composeTestRule.onNodeWithIdentifier(DoNewMeasurementBottomSheetTestIdentifier.WEIGHT_ICON)

    fun assertTitleIsDisplayed() {
        title.assertIsDisplayed()
    }

    fun assertProgressBarIsDisplayed() {
        progressBar.assertIsDisplayed()
    }

    fun assertDescriptionIsDisplayed() {
        description.assertIsDisplayed()
    }

    fun assertBloodPressureIconIsDisplayed() {
        bloodPressureIcon.assertIsDisplayed()
    }

    fun assertWeightIconIsDisplayed() {
        weightIcon.assertIsDisplayed()
    }
}
