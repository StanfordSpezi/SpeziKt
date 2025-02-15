package edu.stanford.bdh.heartbeat.app.fake

import edu.stanford.bdh.heartbeat.app.choir.ChoirRepository
import edu.stanford.bdh.heartbeat.app.choir.api.types.AssessmentStep
import edu.stanford.bdh.heartbeat.app.choir.api.types.AssessmentSubmit
import edu.stanford.bdh.heartbeat.app.choir.api.types.Onboarding
import edu.stanford.bdh.heartbeat.app.choir.api.types.Participant
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FakeChoirRepository @Inject constructor() : ChoirRepository {
    override suspend fun putParticipant(participant: Participant): Result<Unit> {
        return success(Unit)
    }

    override suspend fun unenrollParticipant(): Result<Unit> {
        return success(Unit)
    }

    override suspend fun getOnboarding(): Result<Onboarding> {
        return Result.failure(Error("TODO"))
    }

    override suspend fun continueAssessment(
        token: String,
        submit: AssessmentSubmit,
    ): Result<AssessmentStep> {
        return Result.failure(Error("TODO"))
    }

    private fun <T> success(value: T) = Result.success(value)
}
