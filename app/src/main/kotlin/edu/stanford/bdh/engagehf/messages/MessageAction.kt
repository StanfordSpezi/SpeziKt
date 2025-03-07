package edu.stanford.bdh.engagehf.messages

import edu.stanford.spezi.spezi.ui.resources.StringResource

sealed interface MessageAction {
    data class VideoAction(val sectionId: String, val videoId: String) : MessageAction
    data object MedicationsAction : MessageAction
    data object MeasurementsAction : MessageAction
    data class QuestionnaireAction(val questionnaireId: String) : MessageAction
    data object HealthSummaryAction : MessageAction

    val description: StringResource get() = when (this) {
        is VideoAction -> StringResource("Play Video")
        is MedicationsAction -> StringResource("See Medications")
        is QuestionnaireAction -> StringResource("Start Questionnaire")
        is HealthSummaryAction -> StringResource("See Health Summary")
        is MeasurementsAction -> StringResource("See Heart Health")
    }
}
