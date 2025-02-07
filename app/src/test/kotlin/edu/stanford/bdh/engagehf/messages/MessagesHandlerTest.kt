package edu.stanford.bdh.engagehf.messages

import com.google.common.truth.Truth.assertThat
import edu.stanford.bdh.engagehf.R
import edu.stanford.bdh.engagehf.bluetooth.component.AppScreenEvents
import edu.stanford.bdh.engagehf.education.EngageEducationRepository
import edu.stanford.bdh.engagehf.navigation.AppNavigationEvent
import edu.stanford.bdh.engagehf.navigation.screens.BottomBarItem
import edu.stanford.spezi.core.navigation.Navigator
import edu.stanford.spezi.core.testing.coVerifyNever
import edu.stanford.spezi.core.testing.runTestUnconfined
import edu.stanford.spezi.core.testing.verifyNever
import edu.stanford.spezi.core.utils.MessageNotifier
import edu.stanford.spezi.modules.education.EducationNavigationEvent
import edu.stanford.spezi.modules.education.videos.Video
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import org.junit.Test

class MessagesHandlerTest {
    private val messageRepository = mockk<MessageRepository>(relaxed = true)
    private val engageEducationRepository = mockk<EngageEducationRepository>(relaxed = true)
    private val healthSummaryService = mockk<HealthSummaryService>(relaxed = true)
    private val messageNotifier = mockk<MessageNotifier>(relaxed = true)
    private val appScreenEvents = mockk<AppScreenEvents>(relaxed = true)
    private val navigator = mockk<Navigator>(relaxed = true)
    private val messageAction = MessageAction.MeasurementsAction
    private val messageId = "some-id"
    private val videoSectionId = "some-video-section-id"
    private val videoId = "some-video-id"
    private val message: Message = mockk {
        every { action } returns messageAction
        every { id } returns messageId
        every { isDismissible } returns true
    }

    private val messagesHandler by lazy {
        MessagesHandler(
            appScreenEvents = appScreenEvents,
            healthSummaryService = healthSummaryService,
            engageEducationRepository = engageEducationRepository,
            navigator = navigator,
            messageRepository = messageRepository,
            messageNotifier = messageNotifier,
        )
    }

    @Test
    fun `it should return message updates of the repository`() = runTestUnconfined {
        // given
        val updates: Flow<List<Message>> = emptyFlow()
        every { messageRepository.observeUserMessages() } returns updates

        // when
        val result = messagesHandler.observeUserMessages()

        // then
        assertThat(result).isEqualTo(updates)
    }

    @Test
    fun `it should handle HealthSummaryAction correctly on non error result`() = runTestUnconfined {
        // given
        setup(action = MessageAction.HealthSummaryAction)
        coEvery { healthSummaryService.generateHealthSummaryPdf() } returns Result.success(Unit)

        // when
        messagesHandler.handle(messageId = message.id, isDismissible = message.isDismissible, action = message.action)

        // then
        assertSuccess()
    }

    @Test
    fun `it should not dismiss health summary message on error result`() = runTestUnconfined {
        // given
        setup(action = MessageAction.HealthSummaryAction)
        coEvery {
            healthSummaryService.generateHealthSummaryPdf()
        } returns Result.failure(Throwable())

        // when
        messagesHandler.handle(messageId = message.id, isDismissible = message.isDismissible, action = message.action)

        // then
        assertError()
    }

    @Test
    fun `it should not dismiss video section message on error result`() = runTestUnconfined {
        // given
        setup(action = MessageAction.VideoAction(sectionId = videoSectionId, videoId = videoId))
        coEvery {
            engageEducationRepository.getVideoBySectionAndVideoId(
                sectionId = videoSectionId,
                videoId = videoId,
            )
        } returns Result.failure(Throwable())

        // when
        messagesHandler.handle(messageId = message.id, isDismissible = message.isDismissible, action = message.action)

        // then
        assertError()
    }

    @Test
    fun `it should handle video section action correctly`() = runTestUnconfined {
        val video: Video = mockk()
        val videoSectionAction = MessageAction.VideoAction(sectionId = videoSectionId, videoId = videoId)
        setup(action = videoSectionAction)
        coEvery {
            engageEducationRepository.getVideoBySectionAndVideoId(videoSectionId, videoId)
        } returns Result.success(video)

        // when
        messagesHandler.handle(messageId = message.id, isDismissible = message.isDismissible, action = message.action)

        // then
        verify { navigator.navigateTo(EducationNavigationEvent.VideoSectionClicked(video)) }
        assertSuccess()
    }

    @Test
    fun `it should handle MeasurementsAction correctly`() = runTestUnconfined {
        // given
        setup(action = MessageAction.MeasurementsAction)

        // when
        messagesHandler.handle(messageId = message.id, isDismissible = message.isDismissible, action = message.action)

        // then
        verify { appScreenEvents.emit(AppScreenEvents.Event.DoNewMeasurement) }
        assertSuccess()
    }

    @Test
    fun `it should navigate to questionnaire screen on QuestionnaireAction`() = runTestUnconfined {
        // given
        val questionnaireId = "1"
        setup(action = MessageAction.QuestionnaireAction(questionnaireId))

        // when
        messagesHandler.handle(messageId = message.id, isDismissible = message.isDismissible, action = message.action)

        // then
        verify { navigator.navigateTo(AppNavigationEvent.QuestionnaireScreen(questionnaireId)) }
        assertSuccess()
    }

    @Test
    fun `it should handle medication change action correctly`() = runTestUnconfined {
        // given
        setup(action = MessageAction.MedicationsAction)

        // when
        messagesHandler.handle(messageId = message.id, isDismissible = message.isDismissible, action = message.action)

        // then
        verify { appScreenEvents.emit(AppScreenEvents.Event.NavigateToTab(BottomBarItem.MEDICATION)) }
        assertSuccess()
    }

    @Test
    fun `it should not dismiss non-dismissable messages on success`() = runTestUnconfined {
        // given
        every { message.isDismissible } returns false
        setup(action = MessageAction.MedicationsAction)

        // when
        messagesHandler.handle(messageId = message.id, isDismissible = message.isDismissible, action = message.action)

        // then
        verify { appScreenEvents.emit(AppScreenEvents.Event.NavigateToTab(BottomBarItem.MEDICATION)) }
        coVerifyNever { messageRepository.dismissMessage(messageId = messageId) }
        verifyNever { messageNotifier.notify(R.string.error_while_handling_message_action) }
    }

    private fun assertError() {
        verify(exactly = 1) {
            messageNotifier.notify(R.string.error_while_handling_message_action)
        }
        coVerifyNever { messageRepository.dismissMessage(messageId = messageId) }
    }

    private fun assertSuccess() {
        verifyNever { messageNotifier.notify(R.string.error_while_handling_message_action) }
        coVerify(exactly = 1) { messageRepository.dismissMessage(messageId = messageId) }
    }

    private fun setup(action: MessageAction) {
        every { message.action } returns action
    }
}
