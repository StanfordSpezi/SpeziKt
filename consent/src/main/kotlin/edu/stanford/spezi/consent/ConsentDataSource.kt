package edu.stanford.spezi.consent

import android.graphics.pdf.PdfDocument
import edu.stanford.spezi.core.utils.Standard
import javax.inject.Inject

class ConsentDataSource @Inject constructor() {
    val standard: Standard? = null

    init {
        if (standard !is ConsentConstraint) {
            error("Standard does not conform to ConsentConstraint!")
        }
    }

    suspend fun store(document: suspend () -> PdfDocument, identifier: String) {
        (standard as? ConsentConstraint)?.let { consentConstraint ->
            val export = ConsentDocumentExport(identifier, document)
            consentConstraint.store(export)
        } ?: error("Standard does not conform to ConsentConstraint!")
    }
}
