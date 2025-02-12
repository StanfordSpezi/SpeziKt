package edu.stanford.bdh.heartbeat.app.choir.api.types

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class QuestionType {
    @SerialName("radioset") RADIOSET,

    @SerialName("buttonList") BUTTON_LIST,

    @SerialName("textList") TEXT_LIST,

    @SerialName("bodyMap") BODY_MAP,

    @SerialName("slider") SLIDER,

    @SerialName("numericScale") NUMERIC_SCALE,

    @SerialName("form") FORM,

    @SerialName("thanks") THANKS,

    @SerialName("skip") SKIP,

    @SerialName("close") CLOSE,

    @SerialName("sizedMap") SIZED_MAP,

    @SerialName("customThanks") CUSTOM_THANKS,

    @SerialName("formattedText") FORMATTED_TEXT,

    @SerialName("jsPsych") JS_PSYCH,
}
