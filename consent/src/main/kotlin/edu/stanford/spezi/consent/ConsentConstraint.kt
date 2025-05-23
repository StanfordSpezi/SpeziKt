package edu.stanford.spezi.consent

import edu.stanford.spezi.core.utils.Standard

interface ConsentConstraint : Standard {
    suspend fun store(consent: ConsentDocumentExport)
}
