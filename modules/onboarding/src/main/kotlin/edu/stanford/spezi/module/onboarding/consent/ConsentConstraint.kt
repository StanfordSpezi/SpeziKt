package edu.stanford.spezi.module.onboarding.consent

import edu.stanford.spezi.module.onboarding.views.Standard

interface ConsentConstraint : Standard {
    suspend fun store(consent: ConsentDocumentExport)
}
