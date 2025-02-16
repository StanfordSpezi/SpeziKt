package edu.stanford.bdh.heartbeat.app.choir.api.types

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
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
    /**
     * Implement questionnaire UI elements:
     * Number (i.e. numeric input)
     * Text Area (i.e. bigger text block), requires HTML -> UI conversion
     * Check boxes (i.e. multi-selection)
     * Radios (i.e. single-selection)
     * Heading, requires HTML -> UI conversion
     * Text, requires HTML -> UI conversion
     * DatePicker
     * Dropdown
     */
    @Serializable
    enum class Type {
        /* START - REQUIRED IMPL */
        @SerialName("number") NUMBER, // +

        @SerialName("checkboxes") CHECKBOXES, // +

        @SerialName("radios") RADIOS, // +

        @SerialName("heading") HEADING, // +

        @SerialName("text") TEXT, // +

        @SerialName("dropdown") DROPDOWN, // +

        @SerialName("datePicker") DATE_PICKER, // NO EXAMPLE FOUND IN API RESPONSE

        @SerialName("textArea") TEXT_AREA, // NO EXAMPLE FOUND IN API RESPONSE

        /* END - REQUIRED IMPL */

        @SerialName("videoLink") VIDEO_LINK,

        @SerialName("numericScale") NUMERIC_SCALE,

        @SerialName("textBoxSet") TEXT_BOX_SET,

        @SerialName("collapsibleContentField") COLLAPSIBLE_CONTENT_FIELD,

        @SerialName("radioSetGrid") RADIO_SET_GRID,

        @SerialName("formattedText") FORMATTED_TEXT,

        @SerialName("jsPsych") JS_PSYCH,
    }
}
