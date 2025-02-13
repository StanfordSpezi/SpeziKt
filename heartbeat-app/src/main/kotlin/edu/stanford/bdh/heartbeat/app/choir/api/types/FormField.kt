package edu.stanford.bdh.heartbeat.app.choir.api.types

import android.annotation.SuppressLint
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SuppressLint("UnsafeOptInUsageError")
data class FormField(
    val fieldId: String,
    val type: Type,
    val label: String? = null,
    val required: Boolean? = null,
    val min: String? = null,
    val max: String? = null,
    // val attributes: Map<String, Any>?, TODO: Check whether we actually need this value - if not, we could skip it
    val values: List<FormFieldValue>? = null,
) {
    @Serializable
    enum class Type {
        @SerialName("number") NUMBER,

        @SerialName("textArea") TEXT_AREA,

        @SerialName("checkboxes") CHECKBOXES,

        @SerialName("radios") RADIOS,

        @SerialName("heading") HEADING,

        @SerialName("text") TEXT,

        @SerialName("videoLink") VIDEO_LINK,

        @SerialName("numericScale") NUMERIC_SCALE,

        @SerialName("textBoxSet") TEXT_BOX_SET,

        @SerialName("collapsibleContentField") COLLAPSIBLE_CONTENT_FIELD,

        @SerialName("datePicker") DATE_PICKER,

        @SerialName("dropdown") DROPDOWN,

        @SerialName("radioSetGrid") RADIO_SET_GRID,

        @SerialName("formattedText") FORMATTED_TEXT,

        @SerialName("jsPsych") JS_PSYCH,
    }
}
