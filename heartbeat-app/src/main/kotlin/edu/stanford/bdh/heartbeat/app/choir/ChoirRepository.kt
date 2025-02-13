package edu.stanford.bdh.heartbeat.app.choir

import edu.stanford.bdh.heartbeat.app.choir.api.ChoirApi
import edu.stanford.bdh.heartbeat.app.choir.api.types.AssessmentStep
import edu.stanford.bdh.heartbeat.app.choir.api.types.AssessmentSubmit
import edu.stanford.bdh.heartbeat.app.choir.api.types.Onboarding
import edu.stanford.bdh.heartbeat.app.choir.api.types.Participant
import retrofit2.Response
import javax.inject.Inject

class ChoirRepository @Inject internal constructor(
    private val api: ChoirApi,
) {
    companion object {
        const val SITE_ID = "afib"
    }

    suspend fun putParticipant(participant: Participant) {
        return body(api.putParticipant(SITE_ID, participant))
    }

    suspend fun unenrollParticipant() {
        return body(api.unenrollParticipant(SITE_ID))
    }

    suspend fun getOnboarding(): Onboarding {
        return body(api.getOnboarding(SITE_ID))
    }

    suspend fun continueAssessment(token: String, submit: AssessmentSubmit): AssessmentStep {
        return body(api.continueAssessment(SITE_ID, token, submit))
    }

    private fun <T> body(response: Response<T>): T {
        return response.body()
            ?: error(response.errorBody()?.string() ?: "Unknown API error.")
    }
}
