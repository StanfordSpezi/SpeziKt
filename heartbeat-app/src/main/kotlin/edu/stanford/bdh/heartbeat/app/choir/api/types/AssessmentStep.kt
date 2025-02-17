package edu.stanford.bdh.heartbeat.app.choir.api.types

import kotlinx.serialization.Serializable

@Serializable
data class AssessmentStep(
    val displayStatus: DisplayStatus,
    val question: QuestionPayload,
) {
    @Serializable
    data class QuestionPayload(
        val value1: FormQuestion? = null,
    )
}
