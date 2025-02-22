package edu.stanford.bdh.heartbeat.app.choir

import edu.stanford.bdh.heartbeat.app.choir.api.ChoirApi
import edu.stanford.bdh.heartbeat.app.choir.api.types.AssessmentStep
import edu.stanford.bdh.heartbeat.app.choir.api.types.AssessmentSubmit
import edu.stanford.bdh.heartbeat.app.choir.api.types.Onboarding
import edu.stanford.bdh.heartbeat.app.choir.api.types.Participant
import edu.stanford.spezi.core.logging.speziLogger
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
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
    private val logger by speziLogger()

    private val json = Json {
        prettyPrint = true
    }

    override suspend fun putParticipant(participant: Participant): Result<Unit> {
        logger.i { "Invoking putParticipant for ${json(participant)}" }
        return result(api.putParticipant(SITE_ID, participant))
    }

    override suspend fun unenrollParticipant(): Result<Unit> {
        logger.i { "Invoking unrollParticipant" }
        return result(api.unenrollParticipant(SITE_ID))
    }

    override suspend fun getOnboarding(): Result<Onboarding> {
        logger.i { "Invoking getOnboarding" }
        return result(api.getOnboarding(SITE_ID))
    }

    override suspend fun continueAssessment(
        token: String,
        submit: AssessmentSubmit,
    ): Result<AssessmentStep> {
        logger.i { "Invoking continueAssessment with $token and ${json(submit)}" }
        return result(api.continueAssessment(SITE_ID, token, submit))
    }

    private inline fun <reified T> result(response: Response<T>): Result<T> {
        return if (response.isSuccessful) {
            response.body()?.let {
                logger.i { "Returning successful response ${json(it)}" }
                Result.success(it)
            } ?: Result.failure<T>(Throwable("Empty response body.")).also {
                logger.i { "Returning error response with empty body" }
            }
        } else {
            val errorMessage = response.errorBody()?.string() ?: "Unknown API error."
            val statusCode = response.code()
            val message = response.message()
            val error = Throwable("HTTP $statusCode: $errorMessage, message: $message")
            logger.i { "Received error response $error" }
            Result.failure(error)
        }
    }

    private inline fun <reified T> json(value: T) = json.encodeToString(serializer<T>(), value)

    private companion object {
        const val SITE_ID = "afib"
    }
}
