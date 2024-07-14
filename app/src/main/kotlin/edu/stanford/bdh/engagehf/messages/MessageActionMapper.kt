package edu.stanford.bdh.engagehf.messages

import javax.inject.Inject

data class VideoSectionVideo(val videoSectionId: String, val videoId: String)
data class Questionnaire(val questionnaireId: String)

sealed class Action {
    data class VideoSectionAction(val videoSectionVideo: VideoSectionVideo) : Action()
    data object MedicationsAction : Action()
    data object MeasurementsAction : Action()
    data class QuestionnaireAction(val questionnaire: Questionnaire) : Action()
    data object HealthSummaryAction : Action()
}

internal class MessageActionMapper @Inject constructor() {

    private val videoSectionRegex = Regex("/videoSections/(\\w+)/videos/(\\w+)")
    private val questionnaireRegex = Regex("/questionnaires/(\\w+)")

    fun map(action: String): Result<Action> {
        return runCatching {
            return when {
                videoSectionRegex.matches(action) -> {
                    val matchResult = videoSectionRegex.find(action)
                    val (videoSectionId, videoId) = matchResult!!.destructured
                    Result.success(
                        Action.VideoSectionAction(
                            VideoSectionVideo(
                                videoSectionId,
                                videoId
                            )
                        )
                    )
                }

                action == "/medications" -> Result.success(Action.MedicationsAction)
                action == "/measurements" -> Result.success(Action.MeasurementsAction)
                questionnaireRegex.matches(action) -> {
                    val matchResult = questionnaireRegex.find(action)
                    val (questionnaireId) = matchResult!!.destructured
                    Result.success(Action.QuestionnaireAction(Questionnaire(questionnaireId)))
                }

                action == "/healthSummary" -> Result.success(Action.HealthSummaryAction)
                else -> error("Unknown action type")
            }
        }
    }
}
