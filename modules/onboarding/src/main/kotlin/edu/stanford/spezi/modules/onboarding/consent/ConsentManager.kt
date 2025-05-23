package edu.stanford.spezi.modules.onboarding.consent

interface ConsentManager {
    suspend fun getMarkdownText(): String

    suspend fun onConsented()

    suspend fun onConsentFailure(error: Throwable)
}
