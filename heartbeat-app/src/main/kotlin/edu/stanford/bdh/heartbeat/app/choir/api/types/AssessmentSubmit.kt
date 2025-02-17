package edu.stanford.bdh.heartbeat.app.choir.api.types

import kotlinx.serialization.Serializable

@Serializable
data class AssessmentSubmit(
    val submitStatus: SubmitStatus?,
    val answers: AnswersPayload,
) {
    @Serializable
    data class AnswersPayload(
        val value1: FormAnswer? = null,
    )
}