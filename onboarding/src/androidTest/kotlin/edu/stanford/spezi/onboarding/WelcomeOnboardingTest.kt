package edu.stanford.spezi.onboarding

import androidx.compose.ui.test.junit4.createComposeRule
import edu.stanford.spezi.onboarding.composables.WelcomeOnboardingTestComposable
import edu.stanford.spezi.onboarding.simulators.WelcomeOnboardingSimulator
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class WelcomeOnboardingTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Before
    fun init() {
        composeRule.setContent {
            WelcomeOnboardingTestComposable()
        }
    }

    @Test
    fun testWelcomeOnboarding() {
        welcomeOnboarding {
            WelcomeOnboardingSimulator.assertTextExists("Welcome")
            WelcomeOnboardingSimulator.assertTextExists("Spezi UI Tests")

            WelcomeOnboardingSimulator.assertTextExists("Tortoise")
            WelcomeOnboardingSimulator.assertTextExists("A Tortoise!")

            WelcomeOnboardingSimulator.assertTextExists("Tree")
            WelcomeOnboardingSimulator.assertTextExists("A Tree!")

            WelcomeOnboardingSimulator.assertTextExists("Letter")
            WelcomeOnboardingSimulator.assertTextExists("A letter!")

            WelcomeOnboardingSimulator.assertTextExists("Circle")
            WelcomeOnboardingSimulator.assertTextExists("A circle!")

            WelcomeOnboardingSimulator.clickButton("Learn More")

            WelcomeOnboardingSimulator.assertTextExists("Done")
            WelcomeOnboardingSimulator.assertTextExists("Welcome Onboarding done!")
        }
    }

    private fun welcomeOnboarding(block: WelcomeOnboardingSimulator.() -> Unit) {
        WelcomeOnboardingSimulator(composeRule).apply { block() }
    }
}
