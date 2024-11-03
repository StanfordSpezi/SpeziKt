package edu.stanford.spezi.modules.storage.credential

import kotlinx.serialization.Serializable

@Serializable
data class Credential(
    val username: String,
    val password: String,
    val server: String? = null,
)
