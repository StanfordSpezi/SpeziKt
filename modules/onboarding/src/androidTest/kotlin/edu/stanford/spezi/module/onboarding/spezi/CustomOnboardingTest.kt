package edu.stanford.spezi.module.onboarding.spezi

import androidx.compose.ui.test.junit4.createComposeRule
import edu.stanford.spezi.module.onboarding.spezi.composables.CustomOnboardingTestComposable
import edu.stanford.spezi.module.onboarding.spezi.simulators.CustomOnboardingSimulator
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class CustomOnboardingTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Before
    fun init() {
        composeRule.setContent {
            CustomOnboardingTestComposable()
        }
    }

    @Test
    fun test() {
        customOnboarding {

        }
    }

    private fun customOnboarding(block: CustomOnboardingSimulator.() -> Unit) {
        CustomOnboardingSimulator(composeRule).apply { block() }
    }

}
