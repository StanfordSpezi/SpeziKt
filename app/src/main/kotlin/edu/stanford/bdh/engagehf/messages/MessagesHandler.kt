package edu.stanford.bdh.engagehf.messages

import androidx.lifecycle.AtomicReference
import edu.stanford.bdh.engagehf.R
import edu.stanford.bdh.engagehf.bluetooth.component.AppScreenEvents
import edu.stanford.bdh.engagehf.education.EngageEducationRepository
import edu.stanford.bdh.engagehf.navigation.AppNavigationEvent
import edu.stanford.bdh.engagehf.navigation.screens.BottomBarItem
import edu.stanford.spezi.core.logging.speziLogger
import edu.stanford.spezi.modules.education.EducationNavigationEvent
import edu.stanford.spezi.modules.navigation.Navigator
import edu.stanford.spezi.modules.utils.MessageNotifier
import javax.inject.Inject

class MessagesHandler @Inject constructor(
    private val appScreenEvents: AppScreenEvents,
    private val engageEducationRepository: EngageEducationRepository,
    private val navigator: Navigator,
    private val messageRepository: MessageRepository,
    private val messageNotifier: MessageNotifier,
) {
    private val logger by speziLogger()
    private val pendingMessageId = AtomicReference<String?>(null)

    fun observeUserMessages() = messageRepository.observeUserMessages()

    suspend fun dismiss(messageId: String) {
        messageRepository.dismissMessage(messageId)
    }

    suspend fun handle(messageId: String, isDismissible: Boolean, action: MessageAction?) {
        val failure = runCatching {
            when (action) {
                null -> Unit

                is MessageAction.HealthSummaryAction -> {
                    pendingMessageId.set(messageId.takeIf { isDismissible })
                    val event = AppScreenEvents.Event.HealthSummaryDisplayRequested {
                        if (pendingMessageId.get() == messageId) {
                            pendingMessageId.set(null)
                            dismiss(messageId = messageId)
                        }
                    }
                    appScreenEvents.emit(event)
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
        if (failure == null && isDismissible && pendingMessageId.get() != messageId) {
            dismiss(messageId = messageId)
        } else if (failure != null) {
            logger.e(failure) { "Error while handling message: $messageId" }
            messageNotifier.notify(messageId = R.string.error_while_handling_message_action)
        } else {
            logger.i { "Message $messageId handled successfully" }
        }
    }
}
