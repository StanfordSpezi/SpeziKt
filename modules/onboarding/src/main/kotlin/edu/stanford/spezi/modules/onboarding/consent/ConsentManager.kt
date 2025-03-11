package edu.stanford.spezi.modules.onboarding.consent

/**
 * A interface that needs to be implemented and provided by the app to provide the consent text and handle consent actions.
 * @see edu.stanford.bdh.engagehf.onboarding.EngageConsentManager
 */
interface ConsentManager {
    suspend fun getMarkdownText(): String
    suspend fun onConsented()
    suspend fun onConsentFailure(error: Throwable)
}
