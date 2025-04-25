package edu.stanford.bdh.engagehf.onboarding

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import edu.stanford.bdh.engagehf.R
import edu.stanford.bdh.engagehf.navigation.AppNavigationEvent
import edu.stanford.spezi.modules.navigation.Navigator
import edu.stanford.spezi.modules.onboarding.consent.ConsentManager
import edu.stanford.spezi.modules.utils.MessageNotifier
import javax.inject.Inject

class EngageConsentManager @Inject internal constructor(
    private val navigator: Navigator,
    private val messageNotifier: MessageNotifier,
    @ApplicationContext private val context: Context,
) : ConsentManager {

    override suspend fun getMarkdownText(): String {
        return context.getString(R.string.consent_markdown_text)
    }

    override suspend fun onConsented() {
        navigator.navigateTo(AppNavigationEvent.AppScreen(clearBackStack = true))
    }

    override suspend fun onConsentFailure(error: Throwable) {
        messageNotifier.notify(R.string.generic_error_description)
    }
}
