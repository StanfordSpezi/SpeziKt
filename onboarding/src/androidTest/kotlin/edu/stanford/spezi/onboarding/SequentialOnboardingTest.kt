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
            assertTextExists("Things to know")
            assertTextExists("And you should pay close attention ...")

            assertTextExists("1")
            assertTextExists("A thing to know")
            assertTextDoesNotExist("2")
            assertTextDoesNotExist("Second thing to know")
            assertTextDoesNotExist("3")
            assertTextDoesNotExist("Third thing to know")
            assertTextDoesNotExist("4")
            assertTextDoesNotExist("Now you should know all the things!")

            clickButton("Next")

            assertTextExists("1")
            assertTextExists("A thing to know")
            assertTextExists("2")
            assertTextExists("Second thing to know")
            assertTextDoesNotExist("3")
            assertTextDoesNotExist("Third thing to know")
            assertTextDoesNotExist("4")
            assertTextDoesNotExist("Now you should know all the things!")

            clickButton("Next")

            assertTextExists("1")
            assertTextExists("A thing to know")
            assertTextExists("2")
            assertTextExists("Second thing to know")
            assertTextExists("3")
            assertTextExists("Third thing to know")
            assertTextDoesNotExist("4")
            assertTextDoesNotExist("Now you should know all the things!")

            clickButton("Next")

            assertTextExists("1")
            assertTextExists("A thing to know")
            assertTextExists("2")
            assertTextExists("Second thing to know")
            assertTextExists("3")
            assertTextExists("Third thing to know")
            assertTextExists("4")
            assertTextExists("Now you should know all the things!")

            clickButton("Continue")

            assertTextExists("Done")
            assertTextExists("Sequential Onboarding done!")
        }
    }

    private fun sequentialOnboarding(block: SequentialOnboardingSimulator.() -> Unit) {
        SequentialOnboardingSimulator(composeRule).apply { block() }
    }
}
