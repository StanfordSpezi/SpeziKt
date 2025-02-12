package edu.stanford.bdh.heartbeat.app.choir

import edu.stanford.bdh.heartbeat.app.choir.api.ChoirApi
import edu.stanford.bdh.heartbeat.app.choir.api.types.AssessmentStep
import edu.stanford.bdh.heartbeat.app.choir.api.types.AssessmentSubmit
import edu.stanford.bdh.heartbeat.app.choir.api.types.Onboarding
import javax.inject.Inject

class ChoirRepository @Inject internal constructor(
    private val api: ChoirApi,
) {
    companion object {
        const val SITE_ID = "afib"
    }

    suspend fun getOnboarding(): Onboarding {
        return api.getOnboarding(SITE_ID)
    }

    suspend fun continueAssessment(token: String, submit: AssessmentSubmit): AssessmentStep {
        return api.continueAssessment(SITE_ID, token, submit)
    }
}
