package edu.stanford.spezi.module.onboarding.spezi

import androidx.compose.ui.test.junit4.createComposeRule
import edu.stanford.spezi.module.onboarding.spezi.composables.WelcomeOnboardingTestComposable
import edu.stanford.spezi.module.onboarding.spezi.simulators.WelcomeOnboardingSimulator
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
            assertTextExists("Welcome")
            assertTextExists("Spezi UI Tests")

            assertTextExists("Tortoise")
            assertTextExists("A Tortoise!")

            assertTextExists("Tree")
            assertTextExists("A Tree!")

            assertTextExists("Letter")
            assertTextExists("A letter!")

            assertTextExists("Circle")
            assertTextExists("A circle!")

            clickButton("Learn More")

            assertTextExists("Done")
            assertTextExists("Welcome Onboarding done!")
        }
    }

    private fun welcomeOnboarding(block: WelcomeOnboardingSimulator.() -> Unit) {
        WelcomeOnboardingSimulator(composeRule).apply { block() }
    }
}
