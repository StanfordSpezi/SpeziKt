package edu.stanford.bdh.heartbeat.app.choir.api.types

import kotlinx.serialization.Serializable

@Serializable
data class AssessmentStep(
    val displayStatus: DisplayStatus,
    val question: FormQuestion,
)
