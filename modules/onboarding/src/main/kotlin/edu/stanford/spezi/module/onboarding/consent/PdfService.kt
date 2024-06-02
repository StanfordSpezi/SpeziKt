package edu.stanford.spezi.module.onboarding.consent

interface PdfService {

    suspend fun createPdf(uiState: ConsentUiState): Boolean

}
