package edu.stanford.bdh.engagehf.simulator

import androidx.compose.ui.test.junit4.ComposeTestRule

class OnboardingFlowSimulator(
    composeTestRule: ComposeTestRule,
) {
    private val onboardingScreenSimulator = OnboardingScreenSimulator(composeTestRule)
    private val sequentialOnboardingScreenSimulator = SequentialOnboardingScreenSimulator(composeTestRule)
    private val invitationCodeScreenSimulator = InvitationCodeScreenSimulator(composeTestRule)

    fun onboardingScreen(scope: OnboardingScreenSimulator.() -> Unit) {
        onboardingScreenSimulator.apply(scope)
    }

    fun sequentialOnboarding(scope: SequentialOnboardingScreenSimulator.() -> Unit) {
        sequentialOnboardingScreenSimulator.apply(scope)
    }

    fun invitationCodeScreen(scope: InvitationCodeScreenSimulator.() -> Unit) {
        invitationCodeScreenSimulator.apply(scope)
    }
}
