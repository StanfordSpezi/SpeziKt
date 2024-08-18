package edu.stanford.bdh.engagehf.bluetooth.data.mapper

import edu.stanford.bdh.engagehf.messages.MessagesAction
import edu.stanford.bdh.engagehf.messages.Questionnaire
import edu.stanford.bdh.engagehf.messages.VideoSectionVideo
import javax.inject.Inject

class MessageActionMapper @Inject constructor() {

    companion object {
        private val videoSectionRegex = Regex("/videoSections/(.+)/videos/(.+)")
        private val questionnaireRegex = Regex("/questionnaires/(.+)")
    }

    fun map(action: String): Result<MessagesAction> {
        return runCatching {
            when {
                videoSectionRegex.matches(action) -> {
                    mapVideoSectionAction(action).getOrThrow()
                }

                action == "/medications" -> MessagesAction.MedicationsAction
                action == "/measurements" -> MessagesAction.MeasurementsAction
                questionnaireRegex.matches(action) -> {
                    val matchResult = questionnaireRegex.find(action)
                    val (questionnaireId) = matchResult!!.destructured
                    MessagesAction.QuestionnaireAction(Questionnaire(questionnaireId))
                }

                action == "/healthSummary" -> MessagesAction.HealthSummaryAction
                else -> error("Unknown action type")
            }
        }
    }

    fun mapVideoSectionAction(action: String): Result<MessagesAction.VideoSectionAction> {
        return runCatching {
            val matchResult = videoSectionRegex.find(action)
            val (videoSectionId, videoId) = matchResult!!.destructured
            MessagesAction.VideoSectionAction(
                VideoSectionVideo(
                    videoSectionId,
                    videoId
                )
            )
        }

    }
}
