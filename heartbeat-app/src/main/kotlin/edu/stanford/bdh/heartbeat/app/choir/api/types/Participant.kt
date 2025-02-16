package edu.stanford.bdh.heartbeat.app.choir.api.types

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Participant(
    val id: String? = null,
    val firstName: String,
    val lastName: String,
    val birthDate: String? = null,
    val email: String,
    val homePhone: String? = null,
    val mobilePhone: String? = null,
    val contactPreference: ContactPreference? = null,
) {
    @Serializable
    enum class ContactPreference {
        @SerialName("Text") TEXT,

        @SerialName("Email") EMAIL,
    }
}
