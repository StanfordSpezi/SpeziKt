package edu.stanford.bdh.engagehf.bluetooth

import com.google.common.truth.Truth.assertThat
import edu.stanford.bdh.engagehf.bluetooth.data.mapper.BluetoothUiStateMapper
import edu.stanford.bdh.engagehf.bluetooth.data.mapper.MeasurementToObservationMapper
import edu.stanford.bdh.engagehf.bluetooth.data.models.BluetoothUiState
import edu.stanford.bdh.engagehf.bluetooth.data.repository.ObservationRepository
import edu.stanford.bdh.engagehf.messages.MessageRepository
import edu.stanford.spezi.core.bluetooth.api.BLEService
import edu.stanford.spezi.core.bluetooth.data.model.BLEServiceEvent
import edu.stanford.spezi.core.bluetooth.data.model.BLEServiceState
import edu.stanford.spezi.core.testing.CoroutineTestRule
import edu.stanford.spezi.core.testing.runTestUnconfined
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class BluetoothViewModelTest {
    private val bleService: BLEService = mockk()
    private val uiStateMapper: BluetoothUiStateMapper = mockk()
    private val observationRepository = mockk<ObservationRepository>(relaxed = true)
    private val messageRepository = mockk<MessageRepository>(relaxed = true)
    private val measurementToObservationMapper: MeasurementToObservationMapper =
        mockk(relaxed = true)

    private val bleServiceState = MutableStateFlow<BLEServiceState>(BLEServiceState.Idle)
    private val bleServiceEvents = MutableSharedFlow<BLEServiceEvent>()
    private val readyUiState: BluetoothUiState.Ready = mockk()

    @get:Rule
    val coroutineTestRule = CoroutineTestRule()

    private lateinit var bluetoothViewModel: BluetoothViewModel

    @Before
    fun setup() {
        with(bleService) {
            every { state } returns bleServiceState
            every { events } returns bleServiceEvents
            every { start() } just Runs
            every { stop() } just Runs
        }
        every { uiStateMapper.map(any()) } returns readyUiState
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
    fun `it should handle idle state correctly`() = runTestUnconfined {
        // given
        createViewModel()

        // when
        bleServiceState.emit(BLEServiceState.Idle)

        // then
        assertState(state = BluetoothUiState.Idle)
    }

    @Test
    fun `it should handle scanning state correctly`() = runTestUnconfined {
        // given
        val scanningState = BLEServiceState.Scanning(sessions = mockk())
        createViewModel()

        // when
        bleServiceState.emit(scanningState)

        // then
        verify { uiStateMapper.map(scanningState) }
        assertState(state = readyUiState)
    }

    @Test
    fun `it should handle BluetoothNotEnabled event correctly`() = runTestUnconfined {
        // given
        createViewModel()

        // when
        bleServiceEvents.emit(BLEServiceEvent.BluetoothNotEnabled)

        // then
        assertEvent(event = BluetoothViewModel.Event.EnableBluetooth)
    }

    @Test
    fun `it should handle MissingPermissions event correctly`() = runTestUnconfined {
        // given
        val permissions = listOf("permission1", "permission2")
        val event = BLEServiceEvent.MissingPermissions(permissions)
        createViewModel()

        // when
        bleServiceEvents.emit(event)

        // then
        assertEvent(event = BluetoothViewModel.Event.RequestPermissions(permissions))
    }

    @Test
    fun `it should handle GenericError event correctly`() = runTestUnconfined {
        // given
        val event = BLEServiceEvent.GenericError(mockk())
        createViewModel()

        // when
        bleServiceEvents.emit(event)

        // then
        assertState(state = BluetoothUiState.Error("Something went wrong!"))
    }

    @Test
    fun `it should handle ScanningFailed event correctly`() = runTestUnconfined {
        // given
        val event = BLEServiceEvent.ScanningFailed(42)
        createViewModel()

        // when
        bleServiceEvents.emit(event)

        // then
        assertState(state = BluetoothUiState.Error("Error while scanning for devices"))
    }

    @Test
    fun `it should handle ScanningStarted event correctly`() = runTestUnconfined {
        // given
        val event = BLEServiceEvent.ScanningStarted
        createViewModel()

        // when
        bleServiceEvents.emit(event)

        // then
        assertState(state = BluetoothUiState.Scanning)
    }

    @Test
    fun `it should do nothing on Connected, Disconnected and MeasurementReceived events`() =
        runTestUnconfined {
            // given
            val events = listOf(
                mockk<BLEServiceEvent.Connected>(),
                mockk<BLEServiceEvent.Disconnected>(),
            )
            createViewModel()

            // when
            events.forEach { bleServiceEvents.emit(it) }

            // then
            assertState(state = BluetoothUiState.Idle)
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

    private fun assertState(state: BluetoothUiState) {
        assertThat(bluetoothViewModel.uiState.value.bluetooth).isEqualTo(state)
    }

    private suspend fun assertEvent(event: BluetoothViewModel.Event) {
        assertThat(bluetoothViewModel.events.first()).isEqualTo(event)
    }

    private fun createViewModel() {
        bluetoothViewModel = BluetoothViewModel(
            bleService = bleService,
            uiStateMapper = uiStateMapper,
            observationRepository = observationRepository,
            messageRepository = messageRepository,
            measurementToObservationMapper = measurementToObservationMapper,
        )
    }
}
