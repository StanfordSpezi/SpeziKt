package edu.stanford.bdh.engagehf.phonenumber

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CountryCode(
    val name: String,
    val iso: String,
    @SerialName("dial_code")
    val dialCode: String,
    val emoji: String,
)
