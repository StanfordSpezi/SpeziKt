package edu.stanford.spezi.modules.storage.secure

import kotlinx.serialization.Serializable

@Serializable
data class Credential(
    val username: String,
    val password: String,
)
