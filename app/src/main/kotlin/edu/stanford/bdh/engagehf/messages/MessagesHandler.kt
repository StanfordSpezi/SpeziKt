package edu.stanford.bdh.engagehf.messages

import edu.stanford.bdh.engagehf.R
import edu.stanford.bdh.engagehf.bluetooth.component.AppScreenEvents
import edu.stanford.bdh.engagehf.bluetooth.data.mapper.MessageActionMapper
import edu.stanford.bdh.engagehf.education.EngageEducationRepository
import edu.stanford.bdh.engagehf.navigation.AppNavigationEvent
import edu.stanford.bdh.engagehf.navigation.screens.BottomBarItem
import edu.stanford.spezi.core.logging.speziLogger
import edu.stanford.spezi.core.navigation.Navigator
import edu.stanford.spezi.core.utils.MessageNotifier
import edu.stanford.spezi.modules.education.EducationNavigationEvent
import javax.inject.Inject

@Suppress("LongParameterList")
class MessagesHandler @Inject constructor(
    private val messagesActionMapper: MessageActionMapper,
    private val appScreenEvents: AppScreenEvents,
    private val healthSummaryService: HealthSummaryService,
    private val engageEducationRepository: EngageEducationRepository,
    private val navigator: Navigator,
    private val messageRepository: MessageRepository,
    private val messageNotifier: MessageNotifier,
) {
    private val logger by speziLogger()

    fun observeUserMessages() = messageRepository.observeUserMessages()

    suspend fun handle(message: Message) {
        val actionResult = messagesActionMapper.map(action = message.action)
        var failure = actionResult.exceptionOrNull()
        when (val messagesAction = actionResult.getOrNull()) {
            is MessagesAction.NoAction -> Unit
            is MessagesAction.HealthSummaryAction -> {
                failure = healthSummaryService.generateHealthSummaryPdf().exceptionOrNull()
            }

            is MessagesAction.VideoSectionAction -> {
                val sectionVideo = messagesAction.videoSectionVideo
                failure = engageEducationRepository.getVideoBySectionAndVideoId(
                    sectionId = sectionVideo.videoSectionId,
                    videoId = sectionVideo.videoId,
                ).onSuccess { video ->
                    navigator.navigateTo(EducationNavigationEvent.VideoSectionClicked(video))
                }.exceptionOrNull()
            }

            is MessagesAction.MeasurementsAction -> {
                appScreenEvents.emit(AppScreenEvents.Event.DoNewMeasurement)
            }

            is MessagesAction.MedicationsAction -> {
                appScreenEvents.emit(AppScreenEvents.Event.NavigateToTab(BottomBarItem.MEDICATION))
            }

            is MessagesAction.QuestionnaireAction -> {
                navigator.navigateTo(
                    AppNavigationEvent.QuestionnaireScreen(messagesAction.questionnaireId)
                )
            }
            else -> Unit
        }
        val messageId = message.id
        if (failure == null && message.isDismissible) {
            messageRepository.completeMessage(messageId = messageId)
        } else if (failure != null) {
            logger.e(failure) { "Error while handling message: $messageId" }
            messageNotifier.notify(messageId = R.string.error_while_handling_message_action)
        } else {
            logger.i { "Message $messageId handled successfully" }
        }
    }
}
