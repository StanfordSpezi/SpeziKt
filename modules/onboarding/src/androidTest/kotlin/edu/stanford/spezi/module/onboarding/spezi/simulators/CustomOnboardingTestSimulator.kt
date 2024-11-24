package edu.stanford.spezi.module.onboarding.spezi.simulators

import androidx.compose.ui.test.junit4.ComposeTestRule

class CustomOnboardingSimulator(
    private val composeTestRule: ComposeTestRule
) {
    suspend fun awaitIdle() {
        composeTestRule.awaitIdle()
    }
}
