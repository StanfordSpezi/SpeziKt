package edu.stanford.bdh.engagehf.messages

import edu.stanford.bdh.engagehf.R
import edu.stanford.spezi.ui.StringResource

sealed interface MessageAction {
    data class VideoAction(val sectionId: String, val videoId: String) : MessageAction
    data object MedicationsAction : MessageAction
    data object MeasurementsAction : MessageAction
    data class QuestionnaireAction(val questionnaireId: String) : MessageAction
    data object HealthSummaryAction : MessageAction

    val description: StringResource get() = when (this) {
        is VideoAction -> StringResource(R.string.message_action_play_video)
        is MedicationsAction -> StringResource(R.string.message_action_see_medications)
        is QuestionnaireAction -> StringResource(R.string.message_action_start_questionnaire)
        is HealthSummaryAction -> StringResource(R.string.message_action_see_health_summary)
        is MeasurementsAction -> StringResource(R.string.message_action_see_heart_health)
    }
}
