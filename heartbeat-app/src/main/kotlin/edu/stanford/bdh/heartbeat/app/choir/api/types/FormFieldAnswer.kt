package edu.stanford.bdh.heartbeat.app.choir.api.types

import kotlinx.serialization.Serializable

@Serializable
data class FormFieldAnswer(
    val fieldId: String,
    val choice: List<String>,
)
