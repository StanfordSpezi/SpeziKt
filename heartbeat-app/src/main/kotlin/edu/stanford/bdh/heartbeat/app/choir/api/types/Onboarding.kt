package edu.stanford.bdh.heartbeat.app.choir.api.types

import android.annotation.SuppressLint
import kotlinx.serialization.Serializable

@Serializable
@SuppressLint("UnsafeOptInUsageError")
data class Onboarding(
    val displayStatus: DisplayStatus,
    val question: FormQuestion,
)
