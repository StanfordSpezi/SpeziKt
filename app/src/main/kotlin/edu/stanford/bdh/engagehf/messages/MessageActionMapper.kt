package edu.stanford.bdh.engagehf.messages

import javax.inject.Inject

data class VideoSectionVideo(val videoSectionId: String, val videoId: String)
data class Questionnaire(val questionnaireId: String)

sealed class MessagesAction {
    data class VideoSectionAction(val videoSectionVideo: VideoSectionVideo) : MessagesAction()
    data object MedicationsAction : MessagesAction()
    data object MeasurementsAction : MessagesAction()
    data class QuestionnaireAction(val questionnaire: Questionnaire) : MessagesAction()
    data object HealthSummaryAction : MessagesAction()
}

internal class MessageActionMapper @Inject constructor() {

    private val videoSectionRegex = Regex("/videoSections/(\\w+)/videos/(\\w+)")
    private val questionnaireRegex = Regex("/questionnaires/(\\w+)")

    fun map(action: String): Result<MessagesAction> {
        return runCatching {
            return when {
                videoSectionRegex.matches(action) -> {
                    val matchResult = videoSectionRegex.find(action)
                    val (videoSectionId, videoId) = matchResult!!.destructured
                    Result.success(
                        MessagesAction.VideoSectionAction(
                            VideoSectionVideo(
                                videoSectionId,
                                videoId
                            )
                        )
                    )
                }

                action == "/medications" -> Result.success(MessagesAction.MedicationsAction)
                action == "/measurements" -> Result.success(MessagesAction.MeasurementsAction)
                questionnaireRegex.matches(action) -> {
                    val matchResult = questionnaireRegex.find(action)
                    val (questionnaireId) = matchResult!!.destructured
                    Result.success(MessagesAction.QuestionnaireAction(Questionnaire(questionnaireId)))
                }

                action == "/healthSummary" -> Result.success(MessagesAction.HealthSummaryAction)
                else -> error("Unknown action type")
            }
        }
    }
}
