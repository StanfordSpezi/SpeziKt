package edu.stanford.bdh.engagehf.bluetooth

import android.content.Context
import androidx.health.connect.client.records.BloodPressureRecord
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.records.WeightRecord
import com.google.common.truth.Truth.assertThat
import edu.stanford.bdh.engagehf.bluetooth.component.AppScreenEvents
import edu.stanford.bdh.engagehf.bluetooth.data.mapper.BluetoothUiStateMapper
import edu.stanford.bdh.engagehf.bluetooth.data.models.Action
import edu.stanford.bdh.engagehf.bluetooth.data.models.BluetoothUiState
import edu.stanford.bdh.engagehf.bluetooth.data.models.MeasurementDialogUiState
import edu.stanford.bdh.engagehf.bluetooth.data.models.VitalDisplayData
import edu.stanford.bdh.engagehf.bluetooth.measurements.MeasurementsRepository
import edu.stanford.bdh.engagehf.bluetooth.service.EngageBLEService
import edu.stanford.bdh.engagehf.bluetooth.service.EngageBLEServiceEvent
import edu.stanford.bdh.engagehf.bluetooth.service.EngageBLEServiceState
import edu.stanford.bdh.engagehf.bluetooth.service.Measurement
import edu.stanford.bdh.engagehf.education.EngageEducationRepository
import edu.stanford.bdh.engagehf.messages.HealthSummaryService
import edu.stanford.bdh.engagehf.messages.Message
import edu.stanford.bdh.engagehf.messages.MessageRepository
import edu.stanford.bdh.engagehf.messages.MessageType
import edu.stanford.bdh.engagehf.messages.MessagesAction
import edu.stanford.bdh.engagehf.messages.VideoSectionVideo
import edu.stanford.bdh.engagehf.navigation.AppNavigationEvent
import edu.stanford.bdh.engagehf.navigation.screens.BottomBarItem
import edu.stanford.spezi.core.navigation.Navigator
import edu.stanford.spezi.core.testing.CoroutineTestRule
import edu.stanford.spezi.core.testing.runTestUnconfined
import edu.stanford.spezi.modules.education.EducationNavigationEvent
import edu.stanford.spezi.modules.education.videos.Video
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.ZonedDateTime

class BluetoothViewModelTest {
    private val bleService: EngageBLEService = mockk()
    private val uiStateMapper: BluetoothUiStateMapper = mockk()
    private val measurementsRepository = mockk<MeasurementsRepository>(relaxed = true)
    private val messageRepository = mockk<MessageRepository>(relaxed = true)
    private val engageEducationRepository = mockk<EngageEducationRepository>(relaxed = true)
    private val healthSummaryService = mockk<HealthSummaryService>(relaxed = true)
    private val context: Context = mockk(relaxed = true)

    private val bleServiceState =
        MutableStateFlow<EngageBLEServiceState>(EngageBLEServiceState.Idle)
    private val bleServiceEvents = MutableSharedFlow<EngageBLEServiceEvent>()
    private val readyUiState: BluetoothUiState.Ready = mockk()
    private val appScreenEvents = mockk<AppScreenEvents>(relaxed = true)
    private val navigator = mockk<Navigator>(relaxed = true)
    private val messageAction = "some-action"
    private val messageId = "some-id"
    private val message: Message = mockk {
        every { action } returns messageAction
        every { id } returns messageId
        every { isExpanded } returns false
    }

    @get:Rule
    val coroutineTestRule = CoroutineTestRule()

    private lateinit var bluetoothViewModel: BluetoothViewModel

    @Before
    fun setup() {
        with(bleService) {
            every { state } returns bleServiceState
            every { events } returns bleServiceEvents
            every { start(any()) } just Runs
            every { stop() } just Runs
        }
        with(uiStateMapper) {
            every { mapBleServiceState(any()) } returns readyUiState
            every { mapWeight(any()) } returns mockk()
            every { mapHeartRate(any()) } returns mockk()
            every { mapBloodPressure(any()) } returns mockk()
            every { mapMeasurementDialog(any()) } returns mockk()
            every { mapMessagesAction(any()) } returns Result.failure(Throwable())
        }
        every { context.packageName } returns "some-package"
    }

    @Test
    fun `it should start ble service and start collection on init`() {
        // when
        createViewModel()

        // then
        verify { bleService.start(true) }
        verify { bleService.state }
        verify { bleService.events }
    }

    @Test
    fun `it should start record observation on init`() {
        // when
        createViewModel()

        // then
        coVerify { measurementsRepository.observeWeightRecord() }
        coVerify { measurementsRepository.observeHeartRateRecord() }
        coVerify { measurementsRepository.observeBloodPressureRecord() }
    }

    @Test
    fun `it should start messages observation on init`() {
        // when
        createViewModel()

        // then
        coVerify { messageRepository.observeUserMessages() }
    }

    @Test
    fun `it should handle ble service state updates correctly`() = runTestUnconfined {
        // given
        val updates = listOf(
            EngageBLEServiceState.Idle,
            EngageBLEServiceState.Scanning(emptyList()),
            EngageBLEServiceState.MissingPermissions(emptyList()),
            EngageBLEServiceState.BluetoothNotEnabled,
        )

        createViewModel()
        updates.forEach { update ->
            every { uiStateMapper.mapBleServiceState(update) } returns readyUiState

            // when
            bleServiceState.emit(update)

            // then
            verify { uiStateMapper.mapBleServiceState(update) }
            assertBluetoothUiState(state = readyUiState)
        }
    }

    @Test
    fun `it should handle MeasurementReceived events correctly`() =
        runTestUnconfined {
            // given
            val measurement: Measurement = mockk()
            val event =
                EngageBLEServiceEvent.MeasurementReceived(
                    device = mockk(),
                    measurement = measurement
                )
            val measurementDialog: MeasurementDialogUiState = mockk()
            every { uiStateMapper.mapMeasurementDialog(measurement) } returns measurementDialog

            createViewModel()

            // when
            bleServiceEvents.emit(event)

            // then
            verify { appScreenEvents.emit(AppScreenEvents.Event.CloseBottomSheet) }
            assertBluetoothUiState(state = readyUiState)
            assertThat(bluetoothViewModel.uiState.value.measurementDialog).isEqualTo(
                measurementDialog
            )
        }

    @Test
    fun `it should handle weight updates correctly`() = runTestUnconfined {
        // Given
        val result: Result<WeightRecord?> = mockk()
        val uiState: VitalDisplayData = mockk()
        every { uiStateMapper.mapWeight(result) } returns uiState
        coEvery {
            measurementsRepository.observeWeightRecord()
        } returns flowOf(result)

        // When
        createViewModel()

        // Then
        assertThat(bluetoothViewModel.uiState.value.weight).isEqualTo(uiState)
    }

    @Test
    fun `it should handle blood pressure updates correctly`() = runTestUnconfined {
        // Given
        val result: Result<BloodPressureRecord?> = mockk()
        val uiState: VitalDisplayData = mockk()
        every { uiStateMapper.mapBloodPressure(result) } returns uiState
        coEvery {
            measurementsRepository.observeBloodPressureRecord()
        } returns flowOf(result)

        // When
        createViewModel()

        // Then
        assertThat(bluetoothViewModel.uiState.value.bloodPressure).isEqualTo(uiState)
    }

    @Test
    fun `it should handle heart rate updates correctly`() = runTestUnconfined {
        // Given
        val result: Result<HeartRateRecord?> = mockk()
        val uiState: VitalDisplayData = mockk()
        every { uiStateMapper.mapHeartRate(result) } returns uiState
        coEvery {
            measurementsRepository.observeHeartRateRecord()
        } returns flowOf(result)

        // When
        createViewModel()

        // Then
        assertThat(bluetoothViewModel.uiState.value.heartRate).isEqualTo(uiState)
    }

    @Test
    fun `it should handle message updates correctly`() {
        // given
        val messages = List(10) { mockk<Message>() }
        coEvery { messageRepository.observeUserMessages() } returns flowOf(messages)

        // when
        createViewModel()

        // then
        assertThat(bluetoothViewModel.uiState.value.messages).isEqualTo(messages)
    }

    @Test
    fun `it should stop service on cleared`() {
        // given
        createViewModel()

        // when
        bluetoothViewModel.onCleared()

        // then
        verify { bleService.stop() }
    }

    @Test
    fun `it should handle confirm measurement action correctly`() {
        val measurement: Measurement.Weight = mockk()
        val action = Action.ConfirmMeasurement(measurement = measurement)
        coEvery { measurementsRepository.save(measurement = measurement) } just Runs
        createViewModel()

        // when
        bluetoothViewModel.onAction(action)

        coVerify { measurementsRepository.save(measurement = measurement) }
        val measurementDialog = bluetoothViewModel.uiState.value.measurementDialog
        with(measurementDialog) {
            assertThat(isVisible).isFalse()
            assertThat(this.measurement).isNull()
            assertThat(this.isProcessing).isFalse()
        }
    }

    @Test
    fun `it should handle dismiss dialog correctly`() {
        // given
        val action = Action.DismissDialog
        createViewModel()

        // when
        bluetoothViewModel.onAction(action)

        // then
        assertThat(bluetoothViewModel.uiState.value.measurementDialog.isVisible).isFalse()
    }

    @Test
    fun `it should do nothing on MessageItemClicked with error result`() {
        // given
        val action = Action.MessageItemClicked(message = message)
        every { uiStateMapper.mapMessagesAction(messageAction) } returns Result.failure(Throwable())
        createViewModel()
        val initialState = bluetoothViewModel.uiState.value

        // when
        bluetoothViewModel.onAction(action = action)

        // then
        assertThat(bluetoothViewModel.uiState.value).isEqualTo(initialState)
    }

    @Test
    fun `it should complete message on non error result`() {
        // given
        val action = Action.MessageItemClicked(message = message)
        every {
            uiStateMapper.mapMessagesAction(messageAction)
        } returns Result.success(MessagesAction.HealthSummaryAction)
        createViewModel()

        // when
        bluetoothViewModel.onAction(action = action)

        // then
        coVerify { messageRepository.completeMessage(messageId = messageId) }
    }

    @Test
    fun `it should handle MeasurementsAction correctly`() {
        // given
        val action = Action.MessageItemClicked(message = message)
        every {
            uiStateMapper.mapMessagesAction(messageAction)
        } returns Result.success(MessagesAction.MeasurementsAction)
        createViewModel()

        // when
        bluetoothViewModel.onAction(action = action)

        // then
        verify { appScreenEvents.emit(AppScreenEvents.Event.DoNewMeasurement) }
    }

    @Test
    fun `it should navigate to questionnaire screen on QuestionnaireAction`() {
        // given
        val questionnaireId = "1"
        val action = Action.MessageItemClicked(message = message)
        every {
            uiStateMapper.mapMessagesAction(messageAction)
        } returns Result.success(MessagesAction.QuestionnaireAction(questionnaireId))
        createViewModel()

        // when
        bluetoothViewModel.onAction(action = action)

        // then
        verify { navigator.navigateTo(AppNavigationEvent.QuestionnaireScreen(questionnaireId)) }
    }

    @Test
    fun `it should handle video section action correctly`() = runTestUnconfined {
        val videoSectionId = "some-video-section-id"
        val videoId = "some-video-id"
        val videoSection: VideoSectionVideo = mockk {
            every { this@mockk.videoSectionId } returns videoSectionId
            every { this@mockk.videoId } returns videoId
        }
        val video: Video = mockk()
        val videoSectionAction = MessagesAction.VideoSectionAction(videoSectionVideo = videoSection)
        every { uiStateMapper.mapMessagesAction(messageAction) } returns Result.success(
            videoSectionAction
        )
        coEvery {
            engageEducationRepository.getVideoBySectionAndVideoId(videoSectionId, videoId)
        } returns Result.success(video)
        createViewModel()

        // when
        bluetoothViewModel.onAction(Action.MessageItemClicked(message = message))

        // then
        coVerify { engageEducationRepository.getVideoBySectionAndVideoId(videoSectionId, videoId) }
        verify { navigator.navigateTo(EducationNavigationEvent.VideoSectionClicked(video)) }
    }

    @Test
    fun `it should handle health summary action correctly`() = runTestUnconfined {
        // given
        val action = Action.MessageItemClicked(message = message)
        every {
            uiStateMapper.mapMessagesAction(messageAction)
        } returns Result.success(MessagesAction.HealthSummaryAction)
        createViewModel()

        // when
        bluetoothViewModel.onAction(action = action)

        // then
        coVerify { healthSummaryService.generateHealthSummaryPdf() }
    }

    @Test
    fun `it should handle medication change action correctly`() {
        // given
        val action = Action.MessageItemClicked(message = message)
        every {
            uiStateMapper.mapMessagesAction(messageAction)
        } returns Result.success(MessagesAction.MedicationsAction)
        createViewModel()

        // when
        bluetoothViewModel.onAction(action = action)

        // then
        verify { appScreenEvents.emit(AppScreenEvents.Event.NavigateToTab(BottomBarItem.MEDICATION)) }
    }

    @Test
    fun `it should handle toggle expand action correctly`() {
        // given
        val isExpanded = false
        val message = Message(
            id = messageId,
            dueDate = ZonedDateTime.now(),
            description = "",
            title = "",
            action = "",
            type = MessageType.MedicationChange,
            isExpanded = isExpanded,
        )
        every { this@BluetoothViewModelTest.message.id } returns "new-id"

        coEvery { messageRepository.observeUserMessages() } returns flowOf(
            listOf(
                message,
                this.message
            )
        )
        createViewModel()

        // when
        bluetoothViewModel.onAction(Action.ToggleExpand(message))

        // then
        assertThat(
            bluetoothViewModel.uiState.value.messages.first().isExpanded
        ).isEqualTo(isExpanded.not())
    }

    @Test
    fun `it should handle permission granted action correctly`() = runTestUnconfined {
        // given
        val permissions = listOf("permission1")
        val state = EngageBLEServiceState.MissingPermissions(permissions)
        createViewModel()
        bleServiceState.emit(state)

        // when
        bluetoothViewModel.onAction(Action.PermissionGranted(permission = permissions.first()))

        // then
        verify(exactly = 2) { bleService.start(true) }
    }

    @Test
    fun `it should handle resumed action correctly`() = runTestUnconfined {
        // given
        createViewModel()

        // when
        bluetoothViewModel.onAction(Action.Resumed)

        // then
        verify(exactly = 2) { bleService.start(true) }
    }

    private fun assertBluetoothUiState(state: BluetoothUiState) {
        assertThat(bluetoothViewModel.uiState.value.bluetooth).isEqualTo(state)
    }

    private fun createViewModel() {
        bluetoothViewModel = BluetoothViewModel(
            bleService = bleService,
            uiStateMapper = uiStateMapper,
            measurementsRepository = measurementsRepository,
            messageRepository = messageRepository,
            appScreenEvents = appScreenEvents,
            navigator = navigator,
            engageEducationRepository = engageEducationRepository,
            healthSummaryService = healthSummaryService,
            context = context,
        )
    }
}
