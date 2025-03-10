package edu.stanford.bdh.engagehf.simulator

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.ComposeTestRule
import edu.stanford.spezi.modules.onboarding.invitation.InvitationCodeScreenTestIdentifier
import edu.stanford.spezi.ui.testing.onNodeWithIdentifier
import edu.stanford.spezi.ui.testing.waitNode

class InvitationCodeScreenSimulator(
    private val composeTestRule: ComposeTestRule,
) {
    private val root = composeTestRule.onNodeWithIdentifier(InvitationCodeScreenTestIdentifier.ROOT)
    private val title =
        composeTestRule.onNodeWithIdentifier(InvitationCodeScreenTestIdentifier.TITLE)
    private val description = composeTestRule.onNodeWithIdentifier(
        InvitationCodeScreenTestIdentifier.DESCRIPTION
    )
    private val mainButton =
        composeTestRule.onNodeWithIdentifier(InvitationCodeScreenTestIdentifier.MAIN_ACTION_BUTTON)
    private val secondaryButton =
        composeTestRule.onNodeWithIdentifier(InvitationCodeScreenTestIdentifier.SECONDARY_ACTION_BUTTON)

    fun assertIsDisplayed() {
        composeTestRule.waitNode(InvitationCodeScreenTestIdentifier.ROOT)
        root.assertIsDisplayed()
    }

    fun assertTitle(text: String) {
        title
            .assertIsDisplayed()
            .assertTextEquals(text)
    }

    fun assertDescription(text: String) {
        description
            .assertIsDisplayed()
            .assertTextEquals(text)
    }

    fun assertMainButtonDisplayed() {
        mainButton.assertIsDisplayed()
    }

    fun assertSecondaryButtonDisplayed() {
        secondaryButton.assertIsDisplayed()
    }
}
