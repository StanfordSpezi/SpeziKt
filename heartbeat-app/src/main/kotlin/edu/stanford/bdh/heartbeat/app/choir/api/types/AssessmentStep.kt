package edu.stanford.bdh.heartbeat.app.choir.api.types

import android.annotation.SuppressLint
import kotlinx.serialization.Serializable

@Serializable
@SuppressLint("UnsafeOptInUsageError")
data class AssessmentStep(
    val displayStatus: DisplayStatus,
    val question: QuestionPayload,
) {
    @Serializable
    data class QuestionPayload(
        val value1: FormQuestion? = null,
    )
}
