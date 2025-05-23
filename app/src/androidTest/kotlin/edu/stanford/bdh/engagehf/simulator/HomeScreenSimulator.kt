package edu.stanford.bdh.engagehf.simulator

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.ComposeTestRule
import edu.stanford.bdh.engagehf.bluetooth.screen.HomeScreenTestIdentifier
import edu.stanford.spezi.testing.ui.onNodeWithIdentifier

class HomeScreenSimulator(
    private val composeTestRule: ComposeTestRule,
) {
    private val root = composeTestRule.onNodeWithIdentifier(HomeScreenTestIdentifier.ROOT)

    private val messageTitle =
        composeTestRule.onNodeWithIdentifier(HomeScreenTestIdentifier.MESSAGE_TITLE)

    private val vitalTitle =
        composeTestRule.onNodeWithIdentifier(HomeScreenTestIdentifier.VITAL_TITLE)

    fun assertVital(vitalTitle: String) {
        composeTestRule.onNodeWithIdentifier(HomeScreenTestIdentifier.VITALS, vitalTitle)
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
