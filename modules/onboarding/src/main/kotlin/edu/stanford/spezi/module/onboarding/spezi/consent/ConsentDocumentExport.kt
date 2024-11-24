package edu.stanford.spezi.module.onboarding.spezi.consent

import android.graphics.pdf.PdfDocument

class ConsentDocumentExport(
    private val documentIdentifier: String = Defaults.DOCUMENT_IDENTIFIER,
    private val document: suspend () -> PdfDocument,
) {
    private object Defaults {
        const val DOCUMENT_IDENTIFIER = "ConsentDocument"
    }

    suspend fun createDocument() = document()
}
