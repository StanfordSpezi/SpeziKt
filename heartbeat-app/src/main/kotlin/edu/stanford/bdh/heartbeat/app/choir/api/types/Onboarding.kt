package edu.stanford.bdh.heartbeat.app.choir.api.types

import kotlinx.serialization.Serializable

@Serializable
data class Onboarding(
    val displayStatus: DisplayStatus,
    val question: FormQuestion,
)
