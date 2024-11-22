package edu.stanford.spezi.module.onboarding.consent

import android.graphics.pdf.PdfDocument
import edu.stanford.spezi.core.utils.Standard
import javax.inject.Inject

class MyStandard : Standard

class ConsentDataSource @Inject constructor() {
    // TODO: Inject standard here
    var standard: Standard = MyStandard()

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
