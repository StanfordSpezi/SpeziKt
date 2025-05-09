package edu.stanford.bdh.heartbeat.app.choir.api.types

import kotlinx.serialization.Serializable

@Serializable
data class FormFieldValue(
    val id: String,
    val label: String,
    val fields: List<String>? = null,
)
