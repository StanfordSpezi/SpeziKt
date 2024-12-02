package edu.stanford.spezi.core.design.views

import androidx.compose.ui.test.junit4.createComposeRule
import edu.stanford.spezi.core.design.views.composables.SuspendButtonTestComposable
import edu.stanford.spezi.core.design.views.simulators.SuspendButtonTestSimulator
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class SuspendButtonTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Before
    fun init() {
        composeTestRule.setContent {
            SuspendButtonTestComposable()
        }
    }

    @Test
    fun testSuspendButton() {
        suspendButton {
            clickHelloWorldButton()
            waitForHelloWorldButtonAction()
            resetHelloWorldButtonAction()

            clickHelloThrowingWorldButton()
            assertViewStateAlertAppeared("Error was thrown!")
            dismissViewStateAlert()
            assertHelloThrowingWorldButtonIsEnabled()
        }
    }

    private fun suspendButton(block: SuspendButtonTestSimulator.() -> Unit) {
        SuspendButtonTestSimulator(composeTestRule).apply(block)
    }
}
