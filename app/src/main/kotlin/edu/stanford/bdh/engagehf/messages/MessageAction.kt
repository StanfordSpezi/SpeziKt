package edu.stanford.bdh.engagehf.messages

sealed interface MessageAction {
    data object UnknownAction : MessageAction
    data class VideoAction(val video: Video) : MessageAction
    data object MedicationsAction : MessageAction
    data object MeasurementsAction : MessageAction
    data class QuestionnaireAction(val questionnaireId: String) : MessageAction
    data object HealthSummaryAction : MessageAction
}

data class Video(val sectionId: String, val videoId: String)
