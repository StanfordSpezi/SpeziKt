package edu.stanford.bdh.engagehf.bluetooth.data.mapper

import edu.stanford.bdh.engagehf.messages.MessageAction
import javax.inject.Inject

class MessageActionMapper @Inject constructor() {

    companion object {
        private val videoSectionRegex = Regex("/?videoSections/(.+)/videos/(.+)")
        private val questionnaireRegex = Regex("/?questionnaires/(.+)")
    }

    fun map(action: String?): MessageAction? {
        return runCatching {
            when {
                action.isNullOrBlank() -> error("Empty action")
                videoSectionRegex.matches(action) -> {
                    mapVideoAction(action).getOrThrow()
                }
                action == "medications" -> MessageAction.MedicationsAction
                action == "observations" -> MessageAction.MeasurementsAction
                questionnaireRegex.matches(action) -> {
                    val matchResult = questionnaireRegex.find(action)
                    val (questionnaireId) = matchResult!!.destructured
                    MessageAction.QuestionnaireAction(questionnaireId)
                }
                action == "healthSummary" -> MessageAction.HealthSummaryAction
                else -> error("Unknown action type")
            }
        }.getOrNull()
    }

    fun mapVideoAction(action: String): Result<MessageAction.VideoAction> {
        return runCatching {
            val matchResult = videoSectionRegex.find(action)
            val (sectionId, videoId) = matchResult!!.destructured
            MessageAction.VideoAction(
                sectionId = sectionId,
                videoId = videoId
            )
        }
    }
}
