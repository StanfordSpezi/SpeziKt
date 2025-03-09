package edu.stanford.bdh.heartbeat.app.choir.api

import edu.stanford.bdh.heartbeat.app.choir.api.types.AssessmentStep
import edu.stanford.bdh.heartbeat.app.choir.api.types.AssessmentSubmit
import edu.stanford.bdh.heartbeat.app.choir.api.types.Participant
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ChoirApi {
    @PUT("$BASE/participant")
    suspend fun putParticipant(
        @Body body: Participant,
    ): Response<Unit>

    @DELETE("$BASE/participant")
    suspend fun unenrollParticipant(): Response<Unit>

    @GET("$BASE/onboarding")
    suspend fun getOnboarding(): Response<AssessmentStep>

    @GET("$BASE/assessments/{surveyToken}/start")
    suspend fun startAssessment(
        @Path("surveyToken") surveyToken: String,
    ): Response<AssessmentStep>

    @POST("$BASE/assessments/{surveyToken}/continue")
    suspend fun continueAssessment(
        @Path("surveyToken") surveyToken: String,
        @Body body: AssessmentSubmit,
    ): Response<AssessmentStep>

    private companion object {
        const val BASE = "v1/sites/afib"
    }
}
