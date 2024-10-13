package edu.stanford.spezi.modules.storage.secure

import kotlinx.serialization.Serializable

@Serializable
data class Credentials(
    val username: String,
    val password: String,
)
