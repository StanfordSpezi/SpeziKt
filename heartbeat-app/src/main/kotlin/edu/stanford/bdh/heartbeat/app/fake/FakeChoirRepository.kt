package edu.stanford.bdh.heartbeat.app.fake

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import edu.stanford.bdh.heartbeat.app.R
import edu.stanford.bdh.heartbeat.app.choir.ChoirRepository
import edu.stanford.bdh.heartbeat.app.choir.api.types.AssessmentStep
import edu.stanford.bdh.heartbeat.app.choir.api.types.AssessmentSubmit
import edu.stanford.bdh.heartbeat.app.choir.api.types.Participant
import edu.stanford.spezi.core.logging.speziLogger
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@Serializable
private data class FakeData(
    val onboarding: AssessmentStep,
    val assessmentSteps: List<AssessmentStep>,
)

@Singleton
class FakeChoirRepository @Inject constructor(
    @ApplicationContext context: Context,
) : ChoirRepository, FakeComponent {
    private val logger by speziLogger()

    private val fakeData by lazy {
        context.resources.openRawResource(R.raw.fake_data)
            .bufferedReader()
            .use { it.readText() }
            .let { Json.decodeFromString<FakeData>(it) }
    }
    private var nextAssessmentIndex = 0

    override suspend fun putParticipant(participant: Participant): Result<Unit> {
        return success(Unit)
    }

    override suspend fun unenrollParticipant(): Result<Unit> {
        return success(Unit)
    }

    override suspend fun getOnboarding(): Result<AssessmentStep> {
        delay()
        return success(fakeData.onboarding)
    }

    override suspend fun startAssessment(token: String): Result<AssessmentStep> {
        delay()
        return success(fakeData.onboarding)
    }

    override suspend fun continueAssessment(
        token: String,
        submit: AssessmentSubmit,
    ): Result<AssessmentStep> {
        logger.i { "Processing answers: ${submit.answers?.fieldAnswers}" }
        val index = if (submit.submitStatus?.backRequest == true) nextAssessmentIndex - 2 else nextAssessmentIndex
        val result = fakeData
            .assessmentSteps
            .getOrNull(index) ?: return Result.failure(Error("Done"))

        delay()
        return Result.success(result).also {
            nextAssessmentIndex = fakeData.assessmentSteps.indexOf(result) + 1
        }
    }

    private fun <T> success(value: T) = Result.success(value)
}
