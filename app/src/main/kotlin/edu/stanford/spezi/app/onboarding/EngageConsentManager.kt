package edu.stanford.spezi.app.onboarding

import edu.stanford.spezi.app.navigation.AppNavigationEvent
import edu.stanford.spezi.core.navigation.Navigator
import edu.stanford.spezi.module.onboarding.consent.ConsentManager
import edu.stanford.spezi.module.onboarding.consent.ConsentUiState
import edu.stanford.spezi.module.onboarding.consent.PdfCreationService
import edu.stanford.spezi.module.onboarding.consent.PdfService
import javax.inject.Inject

class EngageConsentManager @Inject internal constructor(
    private val pdfService: PdfService,
    private val navigator: Navigator,
    private val pdfCreationService: PdfCreationService,
) : ConsentManager {

    override suspend fun getMarkdownText(): String {
        return """
        # Consent
        The ENGAGE-HF Android Mobile Application will connect to external devices via Bluetooth to record personal health information, including weight, heart rate, and blood pressure.
            
        Your personal information will only be shared with the research team conducting the study.
        """.trimIndent()
    }

    override suspend fun onConsented(uiState: ConsentUiState): Result<Unit> = runCatching {
        val pdfBytes = pdfCreationService.createPdf(uiState)
        if (pdfService.uploadPdf(pdfBytes).getOrThrow()) {
            navigator.navigateTo(AppNavigationEvent.BluetoothScreen)
        } else {
            error("Upload went wrong")
        }
    }
}
