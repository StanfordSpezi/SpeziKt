package edu.stanford.bdh.heartbeat.app.choir.api.types

import android.annotation.SuppressLint
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SuppressLint("UnsafeOptInUsageError")
data class DisplayStatus(
    val compatLevel: String? = null,
    val questionId: String,
    val questionType: QuestionType,
    val surveyToken: String? = null,
    val stepNumber: String? = null,
    val progress: Double? = null,
    val surveyProviderId: String? = null,
    val surveySectionId: String? = null,
    val surveySystemName: String? = null,
    val serverValidationMessage: String? = null,
    val sessionToken: String? = null,
    val sessionStatus: SessionStatus,
    val resumeToken: String? = null,
    val resumeTimeoutMillis: String? = null,
    val styleSheetName: String? = null,
    val pageTitle: String? = null,
    val locale: String,
    val showBack: Boolean? = null,
) {
    @Serializable
    enum class SessionStatus {
        @SerialName("tokenLookup") TOKEN_LOOKUP,

        @SerialName("tokenLookupInvalid") TOKEN_LOOKUP_INVALID,

        @SerialName("question") QUESTION,

        @SerialName("retry") RETRY,

        @SerialName("clearSession") CLEAR_SESSION,
    }
}
