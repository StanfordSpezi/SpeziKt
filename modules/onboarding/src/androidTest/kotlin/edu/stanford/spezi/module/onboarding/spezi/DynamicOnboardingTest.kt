package edu.stanford.spezi.module.onboarding.spezi

import androidx.compose.ui.test.junit4.createComposeRule
import edu.stanford.spezi.module.onboarding.spezi.composables.DynamicOnboardingTestComposable
import edu.stanford.spezi.module.onboarding.spezi.simulators.DynamicOnboardingSimulator
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class DynamicOnboardingTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Before
    fun init() {
        composeRule.setContent {
            DynamicOnboardingTestComposable()
        }
    }

    @Test
    fun testDynamicOnboardingFlowONE() {
        dynamicOnboarding {
            assertTextExists("START")
            clickButton("ONE")
            assertTextExists("TITLE: ONE")
            clickButton("Next")
            assertTextExists("TITLE: TWO")
            clickButton("Next")
            assertTextExists("Done")
            assertTextExists("Dynamic Onboarding done!")
        }
    }

    @Test
    fun testDynamicOnboardingFlowTWO() {
        dynamicOnboarding {
            assertTextExists("START")
            clickButton("TWO")
            assertTextExists("TITLE: TWO")
            clickButton("Next")
            assertTextExists("Done")
            assertTextExists("Dynamic Onboarding done!")
        }
    }

    @Test
    fun testDynamicOnboardingFlowTHREE() {
        dynamicOnboarding {
            assertTextExists("START")
            clickButton("THREE")
            assertTextExists("TITLE: THREE")
            clickButton("Next")
            assertTextExists("TITLE: ONE")
            clickButton("Next")
            assertTextExists("TITLE: TWO")
            clickButton("Next")
            assertTextExists("Done")
            assertTextExists("Dynamic Onboarding done!")
        }
    }

    @Test
    fun testDynamicOnboardingFlowNext() {
        dynamicOnboarding {
            assertTextExists("START")
            clickButton("Next")
            composeRule.waitForIdle()
            assertTextExists("TITLE: ONE")
            clickButton("Next")
            assertTextExists("TITLE: TWO")
            clickButton("Next")
            assertTextExists("Done")
            assertTextExists("Dynamic Onboarding done!")
        }
    }

    private fun dynamicOnboarding(block: DynamicOnboardingSimulator.() -> Unit) {
        DynamicOnboardingSimulator(composeRule).apply { block() }
    }
}
