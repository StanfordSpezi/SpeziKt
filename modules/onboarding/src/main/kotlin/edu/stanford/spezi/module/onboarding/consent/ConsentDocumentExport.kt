package edu.stanford.spezi.module.onboarding.consent

import android.graphics.pdf.PdfDocument

class ConsentDocumentExport(
    private val documentIdentifier: String,
    private val document: suspend () -> PdfDocument
) {
    suspend fun createDocument() = document()
}