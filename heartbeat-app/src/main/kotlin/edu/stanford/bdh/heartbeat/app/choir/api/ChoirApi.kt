package edu.stanford.bdh.heartbeat.app.choir.api

import edu.stanford.bdh.heartbeat.app.choir.api.types.AssessmentStep
import edu.stanford.bdh.heartbeat.app.choir.api.types.AssessmentSubmit
import edu.stanford.bdh.heartbeat.app.choir.api.types.Onboarding
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ChoirApi {
    @GET("sites/{siteId}/onboarding")
    suspend fun getOnboarding(
        @Path("siteId") siteId: String,
    ): Onboarding

    @POST("sites/{siteId}/assessment/{assessmentToken}/continue")
    suspend fun continueAssessment(
        @Path("siteId") siteId: String,
        @Path("assessmentToken") assessmentToken: String,
        @Body body: AssessmentSubmit,
    ): AssessmentStep
}
