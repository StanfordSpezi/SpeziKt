package edu.stanford.bdh.heartbeat.app.choir.api.types

import kotlinx.serialization.Serializable

@Serializable
data class FormAnswer(
    val fieldAnswers: List<FormFieldAnswer>? = null,
)
