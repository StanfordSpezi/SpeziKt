package edu.stanford.spezi.storage.credential

import android.annotation.SuppressLint
import kotlinx.serialization.Serializable

@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class Credential(
    val username: String,
    val password: String,
    val server: String? = null,
)
