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
import edu.stanford.bdh.engagehf.bluetooth.data.models.MessageUiModel
import edu.stanford.bdh.engagehf.bluetooth.data.models.VitalDisplayData
import edu.stanford.bdh.engagehf.bluetooth.measurements.MeasurementsRepository
import edu.stanford.bdh.engagehf.bluetooth.service.EngageBLEService
import edu.stanford.bdh.engagehf.bluetooth.service.EngageBLEServiceEvent
import edu.stanford.bdh.engagehf.bluetooth.service.EngageBLEServiceState
import edu.stanford.bdh.engagehf.bluetooth.service.Measurement
import edu.stanford.bdh.engagehf.messages.Message
import edu.stanford.bdh.engagehf.messages.MessageAction
import edu.stanford.bdh.engagehf.messages.MessagesHandler
import edu.stanford.bdh.engagehf.navigation.screens.BottomBarItem
import edu.stanford.spezi.core.notification.NotificationPermissions
import edu.stanford.spezi.core.testing.CoroutineTestRule
import edu.stanford.spezi.core.testing.runTestUnconfined
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

class HomeViewModelTest {
    private val bleService: EngageBLEService = mockk()
    private val uiStateMapper: BluetoothUiStateMapper = mockk()
    private val measurementsRepository = mockk<MeasurementsRepository>(relaxed = true)
    private val context: Context = mockk(relaxed = true)
    private val messagesHandler = mockk<MessagesHandler>(relaxed = true)
    private val notificationPermissions = mockk<NotificationPermissions>(relaxed = true)

    private val bleServiceState =
        MutableStateFlow<EngageBLEServiceState>(EngageBLEServiceState.Idle)
    private val bleServiceEvents = MutableSharedFlow<EngageBLEServiceEvent>()
    private val readyUiState: BluetoothUiState.Ready = mockk()
    private val appScreenEvents = mockk<AppScreenEvents>(relaxed = true)
    private val messageId = "some-id"
    private val message: Message = mockk {
        every { id } returns messageId
    }

    @get:Rule
    val coroutineTestRule = CoroutineTestRule()

    private lateinit var viewModel: HomeViewModel

    @Before
    fun setup() {
        with(bleService) {
            every { state } returns bleServiceState
            every { events } returns bleServiceEvents
            every { start() } just Runs
            every { stop() } just Runs
        }
        with(uiStateMapper) {
            every { mapBleServiceState(any()) } returns readyUiState
            every { mapWeight(any()) } returns mockk()
            every { mapHeartRate(any()) } returns mockk()
            every { mapBloodPressure(any()) } returns mockk()
            every { mapMeasurementDialog(any()) } returns mockk()
        }
        every { context.packageName } returns "some-package"
    }

    @Test
    fun `it should indicate notification permissions in initial ui state`() {
        // given
        val permissions = setOf("permission1", "permission2")
        every { notificationPermissions.getRequiredPermissions() } returns permissions
        createViewModel()

        // when
        val uiState = viewModel.uiState.value

        // then
        assertThat(uiState.missingPermissions).isEqualTo(permissions)
    }

    @Test
    fun `it should start ble service and start collection on init`() {
        // when
        createViewModel()

        // then
        verify { bleService.start() }
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
        verify { messagesHandler.observeUserMessages() }
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
            assertThat(viewModel.uiState.value.measurementDialog).isEqualTo(
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
        assertThat(viewModel.uiState.value.weight).isEqualTo(uiState)
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
        assertThat(viewModel.uiState.value.bloodPressure).isEqualTo(uiState)
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
        assertThat(viewModel.uiState.value.heartRate).isEqualTo(uiState)
    }

    @Test
    fun `it should handle message updates correctly`() {
        // given
        val messages = List(10) { mockk<Message>() }
        every { messagesHandler.observeUserMessages() } returns flowOf(messages)

        // when
        createViewModel()

        // then
        assertThat(viewModel.uiState.value.messages.map { it.message }).isEqualTo(messages)
    }

    @Test
    fun `it should stop service on cleared`() {
        // given
        createViewModel()

        // when
        viewModel.onCleared()

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
        viewModel.onAction(action)

        coVerify { measurementsRepository.save(measurement = measurement) }
        val measurementDialog = viewModel.uiState.value.measurementDialog
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
        viewModel.onAction(action)

        // then
        assertThat(viewModel.uiState.value.measurementDialog.isVisible).isFalse()
    }

    @Test
    fun `it should invoke messages handler on message item clicked`() {
        // given
        val message: Message = mockk()
        val action = Action.MessageItemClicked(message = MessageUiModel(message))
        createViewModel()

        // when
        viewModel.onAction(action = action)

        // then
        coVerify { messagesHandler.handle(message) }
    }

    @Test
    fun `it should handle BLEDevicePairing correctly`() {
        // given
        val action = Action.BLEDevicePairing
        createViewModel()

        // when
        viewModel.onAction(action = action)

        // then
        verify { appScreenEvents.emit(AppScreenEvents.Event.BLEDevicePairingBottomSheet) }
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
            action = MessageAction.UnknownAction,
        )
        val model = MessageUiModel(
            message = message,
            isExpanded = isExpanded
        )
        every { this@HomeViewModelTest.message.id } returns "new-id"

        every { messagesHandler.observeUserMessages() } returns flowOf(
            listOf(
                message,
                this.message
            )
        )
        createViewModel()

        // when
        viewModel.onAction(Action.ToggleExpand(model))

        // then
        assertThat(
            viewModel.uiState.value.messages.first().isExpanded
        ).isEqualTo(isExpanded.not())
    }

    @Test
    fun `it should handle permission result action correctly`() = runTestUnconfined {
        // given
        val permissions = listOf("permission1")
        val state = EngageBLEServiceState.MissingPermissions(permissions)
        createViewModel()
        bleServiceState.emit(state)
        val initialState = viewModel.uiState.value

        // when
        viewModel.onAction(Action.PermissionResult(permission = permissions.first()))
        val newState = viewModel.uiState.value

        // then
        assertThat(initialState.missingPermissions).isEqualTo(permissions.toSet())
        assertThat(newState.missingPermissions).isEmpty()
        verify(exactly = 2) { bleService.start() }
    }

    @Test
    fun `it should handle resumed action correctly`() = runTestUnconfined {
        // given
        createViewModel()

        // when
        viewModel.onAction(Action.Resumed)

        // then
        verify(exactly = 2) { bleService.start() }
    }

    @Test
    fun `it should handle VitalsCardClicked action correctly`() {
        // given
        val action = Action.VitalsCardClicked
        createViewModel()

        // when
        viewModel.onAction(action)

        // then
        verify {
            appScreenEvents.emit(
                AppScreenEvents.Event.NavigateToTab(
                    BottomBarItem.HEART_HEALTH
                )
            )
        }
    }

    private fun assertBluetoothUiState(state: BluetoothUiState) {
        assertThat(viewModel.uiState.value.bluetooth).isEqualTo(state)
    }

    private fun createViewModel() {
        viewModel = HomeViewModel(
            bleService = bleService,
            uiStateMapper = uiStateMapper,
            measurementsRepository = measurementsRepository,
            appScreenEvents = appScreenEvents,
            context = context,
            messagesHandler = messagesHandler,
            notificationPermissions = notificationPermissions,
        )
    }
}
