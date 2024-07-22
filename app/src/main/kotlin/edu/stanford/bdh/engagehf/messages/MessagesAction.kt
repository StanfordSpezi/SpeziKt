package edu.stanford.bdh.engagehf.messages

sealed class MessagesAction {
    data class VideoSectionAction(val videoSectionVideo: VideoSectionVideo) : MessagesAction()
    data object MedicationsAction : MessagesAction()
    data object MeasurementsAction : MessagesAction()
    data class QuestionnaireAction(val questionnaire: Questionnaire) : MessagesAction()
    data object HealthSummaryAction : MessagesAction()
}

data class VideoSectionVideo(val videoSectionId: String, val videoId: String)
data class Questionnaire(val questionnaireId: String)
