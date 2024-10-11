package edu.stanford.spezi.module.onboarding.consent

/**
 * A interface that needs to be implemented and provided by the app to provide the consent text and handle consent actions.
 */
interface ConsentManager {
    suspend fun getMarkdownText(): String
    suspend fun onConsented()
    suspend fun onConsentFailure(error: Throwable)
}
