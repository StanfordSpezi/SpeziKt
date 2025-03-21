package edu.stanford.bdh.engagehf.onboarding

import edu.stanford.bdh.engagehf.navigation.AppNavigationEvent
import edu.stanford.spezi.modules.navigation.Navigator
import edu.stanford.spezi.modules.onboarding.consent.ConsentManager
import edu.stanford.spezi.modules.utils.MessageNotifier
import javax.inject.Inject

class EngageConsentManager @Inject internal constructor(
    private val navigator: Navigator,
    private val messageNotifier: MessageNotifier,
) : ConsentManager {

    override suspend fun getMarkdownText(): String {
        return """
        # Consent
        The ENGAGE-HF Android Mobile Application will connect to external devices via Bluetooth to record personal health information, including weight, heart rate, and blood pressure.
            
        Your personal information will only be shared with the research team conducting the study.
        """.trimIndent()
    }

    override suspend fun onConsented() {
        navigator.navigateTo(AppNavigationEvent.AppScreen(clearBackStack = true))
    }

    override suspend fun onConsentFailure(error: Throwable) {
        messageNotifier.notify(message = "Something went wrong, failed to submit the consent!")
    }
}
