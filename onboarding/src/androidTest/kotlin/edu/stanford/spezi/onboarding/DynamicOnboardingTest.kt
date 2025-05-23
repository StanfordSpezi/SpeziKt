package edu.stanford.spezi.onboarding

import androidx.compose.ui.test.junit4.createComposeRule
import edu.stanford.spezi.onboarding.composables.DynamicOnboardingTestComposable
import edu.stanford.spezi.onboarding.simulators.DynamicOnboardingSimulator
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
            DynamicOnboardingSimulator.assertTextExists("START")
            DynamicOnboardingSimulator.clickButton("ONE")
            DynamicOnboardingSimulator.assertTextExists("TITLE: ONE")
            DynamicOnboardingSimulator.clickButton("Next")
            DynamicOnboardingSimulator.assertTextExists("TITLE: TWO")
            DynamicOnboardingSimulator.clickButton("Next")
            DynamicOnboardingSimulator.assertTextExists("Done")
            DynamicOnboardingSimulator.assertTextExists("Dynamic Onboarding done!")
        }
    }

    @Test
    fun testDynamicOnboardingFlowTWO() {
        dynamicOnboarding {
            DynamicOnboardingSimulator.assertTextExists("START")
            DynamicOnboardingSimulator.clickButton("TWO")
            DynamicOnboardingSimulator.assertTextExists("TITLE: TWO")
            DynamicOnboardingSimulator.clickButton("Next")
            DynamicOnboardingSimulator.assertTextExists("Done")
            DynamicOnboardingSimulator.assertTextExists("Dynamic Onboarding done!")
        }
    }

    @Test
    fun testDynamicOnboardingFlowTHREE() {
        dynamicOnboarding {
            DynamicOnboardingSimulator.assertTextExists("START")
            DynamicOnboardingSimulator.clickButton("THREE")
            DynamicOnboardingSimulator.assertTextExists("TITLE: THREE")
            DynamicOnboardingSimulator.clickButton("Next")
            DynamicOnboardingSimulator.assertTextExists("TITLE: ONE")
            DynamicOnboardingSimulator.clickButton("Next")
            DynamicOnboardingSimulator.assertTextExists("TITLE: TWO")
            DynamicOnboardingSimulator.clickButton("Next")
            DynamicOnboardingSimulator.assertTextExists("Done")
            DynamicOnboardingSimulator.assertTextExists("Dynamic Onboarding done!")
        }
    }

    @Test
    fun testDynamicOnboardingFlowNext() {
        dynamicOnboarding {
            DynamicOnboardingSimulator.assertTextExists("START")
            DynamicOnboardingSimulator.clickButton("Next")
            composeRule.waitForIdle()
            DynamicOnboardingSimulator.assertTextExists("TITLE: ONE")
            DynamicOnboardingSimulator.clickButton("Next")
            DynamicOnboardingSimulator.assertTextExists("TITLE: TWO")
            DynamicOnboardingSimulator.clickButton("Next")
            DynamicOnboardingSimulator.assertTextExists("Done")
            DynamicOnboardingSimulator.assertTextExists("Dynamic Onboarding done!")
        }
    }

    private fun dynamicOnboarding(block: DynamicOnboardingSimulator.() -> Unit) {
        DynamicOnboardingSimulator(composeRule).apply { block() }
    }
}
