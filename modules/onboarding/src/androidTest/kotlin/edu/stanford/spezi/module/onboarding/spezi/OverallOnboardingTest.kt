package edu.stanford.spezi.module.onboarding.spezi

import androidx.compose.ui.test.junit4.createComposeRule
import edu.stanford.spezi.module.onboarding.spezi.composables.OverallOnboardingTestComposable
import edu.stanford.spezi.module.onboarding.spezi.simulators.OverallOnboardingSimulator
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class OverallOnboardingTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Before
    fun init() {
        composeRule.setContent {
            OverallOnboardingTestComposable()
        }
    }

    @Test
    fun test() {
        overallOnboarding {

        }
    }

    private fun overallOnboarding(block: OverallOnboardingSimulator.() -> Unit) {
        OverallOnboardingSimulator(composeRule).apply { block() }
    }

}
