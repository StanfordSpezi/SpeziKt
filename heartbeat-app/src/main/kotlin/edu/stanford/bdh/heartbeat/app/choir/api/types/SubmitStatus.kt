package edu.stanford.bdh.heartbeat.app.choir.api.types

import kotlinx.serialization.Serializable

@Serializable
data class SubmitStatus(
    val questionId: String,
    val questionType: QuestionType,
    val stepNumber: Int,
    val surveyProviderId: String? = null,
    val surveySectionId: String? = null,
    val surveySystemName: String? = null,
    val sessionToken: String? = null,
    val callTimeMillis: Long? = null,
    val renderTimeMillis: Long? = null,
    val thinkTimeMillis: Long? = null,
    val retryCount: Int? = null,
    val locale: String,
    val compatLevel: String? = null,
    val backRequest: Boolean? = null
)
