package edu.stanford.bdh.heartbeat.app.choir.api

import edu.stanford.bdh.heartbeat.app.choir.api.types.AssessmentStep
import edu.stanford.bdh.heartbeat.app.choir.api.types.AssessmentSubmit
import edu.stanford.bdh.heartbeat.app.choir.api.types.Onboarding
import edu.stanford.bdh.heartbeat.app.choir.api.types.Participant
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ChoirApi {
    @PUT("sites/{siteId}/participant")
    suspend fun putParticipant(
        @Path("siteId") siteId: String,
        @Body body: Participant,
    ): Response<Unit>

    @DELETE("sites/{siteId}/participant")
    suspend fun unenrollParticipant(
        @Path("siteId") siteId: String,
    ): Response<Unit>

    @GET("sites/{siteId}/onboarding")
    suspend fun getOnboarding(
        @Path("siteId") siteId: String,
    ): Response<Onboarding>

    @POST("sites/{siteId}/assessments/{assessmentToken}/continue")
    suspend fun continueAssessment(
        @Path("siteId") siteId: String,
        @Path("assessmentToken") assessmentToken: String,
        @Body body: AssessmentSubmit,
    ): Response<AssessmentStep>
}
