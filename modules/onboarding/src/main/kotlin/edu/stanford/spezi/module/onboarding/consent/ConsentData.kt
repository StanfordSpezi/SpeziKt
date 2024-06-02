package edu.stanford.spezi.module.onboarding.consent

interface ConsentRepository {
    suspend fun getConsentData(): ConsentData
}

data class ConsentData(
    val markdownText: String = "",
    val onAction: (ConsentUiState) -> Unit
)