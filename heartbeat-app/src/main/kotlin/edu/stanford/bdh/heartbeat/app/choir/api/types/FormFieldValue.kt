package edu.stanford.bdh.heartbeat.app.choir.api.types

import android.annotation.SuppressLint
import kotlinx.serialization.Serializable

@Serializable
@SuppressLint("UnsafeOptInUsageError")
data class FormFieldValue(
    val id: String,
    val label: String,
    val fields: List<String>? = null,
)
