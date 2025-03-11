package edu.stanford.bdh.engagehf.onboarding

import com.google.common.truth.Truth.assertThat
import edu.stanford.bdh.engagehf.navigation.AppNavigationEvent
import edu.stanford.spezi.modules.navigation.Navigator
import edu.stanford.spezi.modules.testing.runTestUnconfined
import edu.stanford.spezi.modules.utils.MessageNotifier
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test

class EngageConsentManagerTest {
    private val navigator: Navigator = mockk()
    private val messageNotifier: MessageNotifier = mockk()
    private val manager = EngageConsentManager(
        navigator = navigator,
        messageNotifier = messageNotifier,
    )

    @Before
    fun setup() {
        every { navigator.navigateTo(AppNavigationEvent.AppScreen(true)) } just Runs
        every { messageNotifier.notify(message = any(), any()) } just Runs
    }

    @Test
    fun `it should return the correct markdown test`() = runTestUnconfined {
        // given
        val expectedText = """
        # Consent
        The ENGAGE-HF Android Mobile Application will connect to external devices via Bluetooth to record personal health information, including weight, heart rate, and blood pressure.
            
        Your personal information will only be shared with the research team conducting the study.
        """.trimIndent()

        // when
        val result = manager.getMarkdownText()

        // then
        assertThat(result).isEqualTo(expectedText)
    }

    @Test
    fun `it should navigate to home screen on consented`() = runTestUnconfined {
        // given
        val navigationEvent = AppNavigationEvent.AppScreen(clearBackStack = true)

        // when
        manager.onConsented()

        // then
        verify { navigator.navigateTo(event = navigationEvent) }
    }

    @Test
    fun `it should notify error message on on consent failure`() = runTestUnconfined {
        // given
        val message = "Something went wrong, failed to submit the consent!"

        // when
        manager.onConsentFailure(error = mockk())

        // then
        verify { messageNotifier.notify(message = message) }
    }
}
