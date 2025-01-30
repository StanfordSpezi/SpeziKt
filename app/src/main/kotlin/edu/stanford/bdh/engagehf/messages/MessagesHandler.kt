package edu.stanford.bdh.engagehf.messages

import edu.stanford.bdh.engagehf.R
import edu.stanford.bdh.engagehf.bluetooth.component.AppScreenEvents
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
    private val appScreenEvents: AppScreenEvents,
    private val healthSummaryService: HealthSummaryService,
    private val engageEducationRepository: EngageEducationRepository,
    private val navigator: Navigator,
    private val messageRepository: MessageRepository,
    private val messageNotifier: MessageNotifier,
) {
    private val logger by speziLogger()

    fun observeUserMessages() = messageRepository.observeUserMessages()

    suspend fun dismiss(message: Message) {
        messageRepository.dismissMessage(message.id)
    }

    suspend fun handle(message: Message) {
        handle(
            messageId = message.id,
            isDismissible = message.isDismissible,
            action = message.action
        )
    }

    suspend fun handle(messageId: String, isDismissible: Boolean, action: MessageAction?) {
        val failure = runCatching {
            when (action) {
                null -> Unit

                is MessageAction.HealthSummaryAction -> {
                    healthSummaryService.generateHealthSummaryPdf().getOrThrow()
                }

                is MessageAction.VideoAction -> {
                    engageEducationRepository.getVideoBySectionAndVideoId(
                        sectionId = action.sectionId,
                        videoId = action.videoId,
                    ).onSuccess { video ->
                        navigator.navigateTo(EducationNavigationEvent.VideoSectionClicked(video))
                    }.getOrThrow()
                }

                is MessageAction.MeasurementsAction -> {
                    appScreenEvents.emit(AppScreenEvents.Event.DoNewMeasurement)
                }

                is MessageAction.MedicationsAction -> {
                    appScreenEvents.emit(AppScreenEvents.Event.NavigateToTab(BottomBarItem.MEDICATION))
                }

                is MessageAction.QuestionnaireAction -> {
                    navigator.navigateTo(
                        AppNavigationEvent.QuestionnaireScreen(action.questionnaireId)
                    )
                }
            }
        }.exceptionOrNull()
        if (failure == null && isDismissible) {
            messageRepository.dismissMessage(messageId = messageId)
        } else if (failure != null) {
            logger.e(failure) { "Error while handling message: $messageId" }
            messageNotifier.notify(messageId = R.string.error_while_handling_message_action)
        } else {
            logger.i { "Message $messageId handled successfully" }
        }
    }
}
