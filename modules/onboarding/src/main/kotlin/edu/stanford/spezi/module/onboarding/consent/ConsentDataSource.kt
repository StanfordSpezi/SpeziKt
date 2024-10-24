package edu.stanford.spezi.module.onboarding.consent

import android.graphics.pdf.PdfDocument
import edu.stanford.spezi.module.onboarding.views.Standard
import javax.inject.Inject

class ConsentDataSource {
    @Inject lateinit var standard: Standard

    init {
        if (standard !is ConsentConstraint) {
            TODO("on iOS: fatalError")
        }
    }

    suspend fun store(document: suspend () -> PdfDocument, identifier: String) {
        (standard as? ConsentConstraint)?.let { consentConstraint ->
            val export = ConsentDocumentExport(identifier, document)
            consentConstraint.store(export)
        } ?: TODO("on iOS: fatalError")
    }
}
