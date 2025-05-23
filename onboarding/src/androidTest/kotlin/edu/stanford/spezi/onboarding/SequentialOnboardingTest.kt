package edu.stanford.spezi.onboarding

import androidx.compose.ui.test.junit4.createComposeRule
import edu.stanford.spezi.onboarding.composables.SequentialOnboardingTestComposable
import edu.stanford.spezi.onboarding.simulators.SequentialOnboardingSimulator
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class SequentialOnboardingTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Before
    fun init() {
        composeRule.setContent {
            SequentialOnboardingTestComposable()
        }
    }

    @Test
    fun testSequentialOnboarding() {
        sequentialOnboarding {
            SequentialOnboardingSimulator.assertTextExists("Things to know")
            SequentialOnboardingSimulator.assertTextExists("And you should pay close attention ...")

            SequentialOnboardingSimulator.assertTextExists("1")
            SequentialOnboardingSimulator.assertTextExists("A thing to know")
            SequentialOnboardingSimulator.assertTextDoesNotExist("2")
            SequentialOnboardingSimulator.assertTextDoesNotExist("Second thing to know")
            SequentialOnboardingSimulator.assertTextDoesNotExist("3")
            SequentialOnboardingSimulator.assertTextDoesNotExist("Third thing to know")
            SequentialOnboardingSimulator.assertTextDoesNotExist("4")
            SequentialOnboardingSimulator.assertTextDoesNotExist("Now you should know all the things!")

            SequentialOnboardingSimulator.clickButton("Next")

            SequentialOnboardingSimulator.assertTextExists("1")
            SequentialOnboardingSimulator.assertTextExists("A thing to know")
            SequentialOnboardingSimulator.assertTextExists("2")
            SequentialOnboardingSimulator.assertTextExists("Second thing to know")
            SequentialOnboardingSimulator.assertTextDoesNotExist("3")
            SequentialOnboardingSimulator.assertTextDoesNotExist("Third thing to know")
            SequentialOnboardingSimulator.assertTextDoesNotExist("4")
            SequentialOnboardingSimulator.assertTextDoesNotExist("Now you should know all the things!")

            SequentialOnboardingSimulator.clickButton("Next")

            SequentialOnboardingSimulator.assertTextExists("1")
            SequentialOnboardingSimulator.assertTextExists("A thing to know")
            SequentialOnboardingSimulator.assertTextExists("2")
            SequentialOnboardingSimulator.assertTextExists("Second thing to know")
            SequentialOnboardingSimulator.assertTextExists("3")
            SequentialOnboardingSimulator.assertTextExists("Third thing to know")
            SequentialOnboardingSimulator.assertTextDoesNotExist("4")
            SequentialOnboardingSimulator.assertTextDoesNotExist("Now you should know all the things!")

            SequentialOnboardingSimulator.clickButton("Next")

            SequentialOnboardingSimulator.assertTextExists("1")
            SequentialOnboardingSimulator.assertTextExists("A thing to know")
            SequentialOnboardingSimulator.assertTextExists("2")
            SequentialOnboardingSimulator.assertTextExists("Second thing to know")
            SequentialOnboardingSimulator.assertTextExists("3")
            SequentialOnboardingSimulator.assertTextExists("Third thing to know")
            SequentialOnboardingSimulator.assertTextExists("4")
            SequentialOnboardingSimulator.assertTextExists("Now you should know all the things!")

            SequentialOnboardingSimulator.clickButton("Continue")

            SequentialOnboardingSimulator.assertTextExists("Done")
            SequentialOnboardingSimulator.assertTextExists("Sequential Onboarding done!")
        }
    }

    private fun sequentialOnboarding(block: SequentialOnboardingSimulator.() -> Unit) {
        SequentialOnboardingSimulator(composeRule).apply { block() }
    }
}
