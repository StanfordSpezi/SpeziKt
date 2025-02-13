package edu.stanford.bdh.heartbeat.app.choir.api.types

import android.annotation.SuppressLint
import kotlinx.serialization.Serializable

@Serializable
@SuppressLint("UnsafeOptInUsageError")
data class AssessmentSubmit(
    val submitStatus: SubmitStatus?,
    val answers: AnswersPayload,
) {
    @Serializable
    data class AnswersPayload(
        val value1: FormAnswer? = null,
    )
}
