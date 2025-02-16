package edu.stanford.bdh.heartbeat.app.fake

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import edu.stanford.bdh.heartbeat.app.R
import edu.stanford.bdh.heartbeat.app.choir.ChoirRepository
import edu.stanford.bdh.heartbeat.app.choir.api.types.AssessmentStep
import edu.stanford.bdh.heartbeat.app.choir.api.types.AssessmentSubmit
import edu.stanford.bdh.heartbeat.app.choir.api.types.Onboarding
import edu.stanford.bdh.heartbeat.app.choir.api.types.Participant
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@Serializable
private data class FakeData(
    val onboarding: Onboarding,
    val assessmentSteps: List<AssessmentStep>,
)

@Singleton
class FakeChoirRepository @Inject constructor(
    @ApplicationContext context: Context,
) : ChoirRepository, FakeComponent {
    private val fakeData by lazy {
        context.resources.openRawResource(R.raw.fake_data)
            .bufferedReader()
            .use { it.readText() }
            .let { Json.decodeFromString<FakeData>(it) }
    }
    private var assessmentIndex = 0

    override suspend fun putParticipant(participant: Participant): Result<Unit> {
        return success(Unit)
    }

    override suspend fun unenrollParticipant(): Result<Unit> {
        return success(Unit)
    }

    override suspend fun getOnboarding(): Result<Onboarding> {
        delay()
        return success(fakeData.onboarding)
    }

    override suspend fun continueAssessment(
        token: String,
        submit: AssessmentSubmit,
    ): Result<AssessmentStep> {
        val result = fakeData
            .assessmentSteps
            .getOrNull(assessmentIndex) ?: return Result.failure(Error("Done"))
        delay()
        return Result.success(result).also {
            assessmentIndex++
        }
    }

    private fun <T> success(value: T) = Result.success(value)
}
