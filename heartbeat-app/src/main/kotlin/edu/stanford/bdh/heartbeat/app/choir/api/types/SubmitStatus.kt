package edu.stanford.bdh.heartbeat.app.choir.api.types

import kotlinx.serialization.Serializable

@Serializable
data class SubmitStatus(
    val questionId: String,
    val questionType: QuestionType,
    val stepNumber: Double,
    val surveyProviderId: String? = null,
    val surveySectionId: String? = null,
    val surveySystemName: String? = null,
    val sessionToken: String? = null,
    val callTimeMillis: Double? = null,
    val renderTimeMillis: Double? = null,
    val thinkTimeMillis: Double? = null,
    val retryCount: Double? = null,
    val locale: String,
    val compatLevel: String? = null,
    val backRequest: Boolean? = null,
)
