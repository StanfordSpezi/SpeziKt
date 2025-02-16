package edu.stanford.bdh.heartbeat.app.survey

import edu.stanford.bdh.heartbeat.app.choir.api.types.FormAnswer
import edu.stanford.bdh.heartbeat.app.choir.api.types.FormFieldAnswer

data class SurveyAnswers(
    private val map: Map<String, AnswerFormat> = emptyMap(),
) {
    fun answer(id: String): AnswerFormat? = map[id]

    fun copyWithChange(id: String, answer: AnswerFormat) =
        copy(map = map.toMutableMap().apply { this[id] = answer })

    fun asFormAnswer() = FormAnswer(
        fieldAnswers = map.map {
            FormFieldAnswer(
                fieldId = it.key,
                choice = it.value.asStringList(),
            )
        }
    )
}

sealed interface AnswerFormat {
    data class Text(val value: String?) : AnswerFormat
    data class Numeric(val value: Double?) : AnswerFormat
    data class Date(val value: java.util.Date?) : AnswerFormat
    data class Weight(val value: Double?) : AnswerFormat
    data class Height(val value: Double?) : AnswerFormat
    data class MultipleChoice(val value: List<ResultValue>?) : AnswerFormat
    data class Image(val value: List<ResultValue>?) : AnswerFormat
    data class Scale(val value: Double?) : AnswerFormat

    fun asStringList(): List<String> = TODO("Not yet implemented")
}

sealed interface ResultValue {
    data class Int(val value: kotlin.Int)
    data class String(val value: kotlin.String)
    data class Date(val value: java.util.Date)
}
