package edu.stanford.spezi.app.onboarding

import edu.stanford.spezi.core.coroutines.di.Dispatching
import edu.stanford.spezi.core.navigation.DefaultNavigationEvent
import edu.stanford.spezi.core.navigation.Navigator
import edu.stanford.spezi.module.onboarding.consent.ConsentData
import edu.stanford.spezi.module.onboarding.consent.ConsentRepository
import edu.stanford.spezi.module.onboarding.consent.PdfService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class EngageConsentRepository @Inject internal constructor(
    private val pdfService: PdfService,
    private val navigator: Navigator,
    @Dispatching.IO private val ioScope: CoroutineScope,
) : ConsentRepository {

    override suspend fun getConsentData(): ConsentData {
        return ConsentData(
            markdownText = """
        # Consent
        The ENGAGE-HF Android Mobile Application will connect to external devices via Bluetooth to record personal health information, including weight, heart rate, and blood pressure.
            
        Your personal information will only be shared with the research team conducting the study.
            """.trimIndent(),
            onAction = {
                ioScope.launch {
                    pdfService.createPdf(it)
                }
                navigator.navigateTo(DefaultNavigationEvent.BluetoothScreen)
            }
        )
    }
}
