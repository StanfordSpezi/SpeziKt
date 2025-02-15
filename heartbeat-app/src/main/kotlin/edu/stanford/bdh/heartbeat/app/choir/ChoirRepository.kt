package edu.stanford.bdh.heartbeat.app.choir

import edu.stanford.bdh.heartbeat.app.choir.api.ChoirApi
import edu.stanford.bdh.heartbeat.app.choir.api.types.AssessmentStep
import edu.stanford.bdh.heartbeat.app.choir.api.types.AssessmentSubmit
import edu.stanford.bdh.heartbeat.app.choir.api.types.Onboarding
import edu.stanford.bdh.heartbeat.app.choir.api.types.Participant
import retrofit2.Response
import javax.inject.Inject

interface ChoirRepository {
    suspend fun putParticipant(participant: Participant): Result<Unit>
    suspend fun unenrollParticipant(): Result<Unit>
    suspend fun getOnboarding(): Result<Onboarding>
    suspend fun continueAssessment(token: String, submit: AssessmentSubmit): Result<AssessmentStep>
}

class ChoirRepositoryImpl @Inject internal constructor(
    private val api: ChoirApi,
) : ChoirRepository {
    companion object {
        const val SITE_ID = "afib"
    }

    override suspend fun putParticipant(participant: Participant): Result<Unit> {
        return result(api.putParticipant(SITE_ID, participant))
    }

    override suspend fun unenrollParticipant(): Result<Unit> {
        return result(api.unenrollParticipant(SITE_ID))
    }

    override suspend fun getOnboarding(): Result<Onboarding> {
        return result(api.getOnboarding(SITE_ID))
    }

    override suspend fun continueAssessment(
        token: String,
        submit: AssessmentSubmit,
    ): Result<AssessmentStep> {
        return result(api.continueAssessment(SITE_ID, token, submit))
    }

    private fun <T> result(response: Response<T>): Result<T> {
        return response.body()?.let { Result.success(it) }
            ?: Result.failure(Error(response.errorBody()?.string() ?: "Unknown API error."))
    }
}
