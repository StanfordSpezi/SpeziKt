package edu.stanford.spezi.module.onboarding.consent

interface ConsentManager {
    suspend fun getMarkdownText(): String
    suspend fun onConsented(uiState: ConsentUiState): Result<Unit>
}
