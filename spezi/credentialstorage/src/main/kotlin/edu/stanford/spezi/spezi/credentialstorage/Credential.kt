package edu.stanford.spezi.spezi.credentialstorage

import android.annotation.SuppressLint
import kotlinx.serialization.Serializable

@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class Credential(
    val username: String,
    val password: String,
    val server: String? = null,
)
