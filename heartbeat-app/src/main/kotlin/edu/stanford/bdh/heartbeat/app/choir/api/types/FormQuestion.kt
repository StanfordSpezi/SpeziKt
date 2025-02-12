package edu.stanford.bdh.heartbeat.app.choir.api.types

import android.annotation.SuppressLint
import kotlinx.serialization.Serializable

@Serializable
@SuppressLint("UnsafeOptInUsageError")
data class FormQuestion(
    val title1: String,
    val title2: String? = null,
    val serverValidationMessage: String? = null,
    val terminal: Boolean? = null,
    val fields: List<FormField>? = null,
)
