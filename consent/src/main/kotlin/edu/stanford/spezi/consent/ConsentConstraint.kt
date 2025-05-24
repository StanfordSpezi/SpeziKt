package edu.stanford.spezi.consent

import edu.stanford.spezi.core.Standard

interface ConsentConstraint : Standard {
    suspend fun store(consent: ConsentDocumentExport)
}
