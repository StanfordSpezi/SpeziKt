package edu.stanford.bdh.engagehf.simulator

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.ComposeTestRule
import edu.stanford.bdh.engagehf.bluetooth.screen.BluetoothScreenTestIdentifier
import edu.stanford.spezi.core.testing.onNodeWithIdentifier

class BluetoothScreenSimulator(
    private val composeTestRule: ComposeTestRule,
) {
    private val root = composeTestRule.onNodeWithIdentifier(BluetoothScreenTestIdentifier.ROOT)

    private val messageTitle =
        composeTestRule.onNodeWithIdentifier(BluetoothScreenTestIdentifier.MESSAGE_TITLE)

    private val vitalTitle =
        composeTestRule.onNodeWithIdentifier(BluetoothScreenTestIdentifier.VITAL_TITLE)

    fun assertVital(vitalTitle: String) {
        composeTestRule.onNodeWithIdentifier(BluetoothScreenTestIdentifier.VITALS, vitalTitle)
            .assertIsDisplayed()
    }

    fun assertIsDisplayed() {
        root.assertIsDisplayed()
    }

    fun assertMessageTitle(text: String) {
        messageTitle.assertIsDisplayed().assertTextEquals(text)
    }

    fun assertVitalTitle(text: String) {
        vitalTitle.assertIsDisplayed().assertTextEquals(text)
    }
}
