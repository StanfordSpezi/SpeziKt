package edu.stanford.bdh.engagehf.bluetooth

import androidx.health.connect.client.records.BloodPressureRecord
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.records.WeightRecord
import androidx.health.connect.client.units.Pressure
import com.google.common.truth.Truth.assertThat
import edu.stanford.bdh.engagehf.bluetooth.component.BottomSheetEvents
import edu.stanford.bdh.engagehf.bluetooth.data.mapper.BluetoothUiStateMapper
import edu.stanford.bdh.engagehf.bluetooth.data.models.BluetoothUiState
import edu.stanford.bdh.engagehf.messages.MessageRepository
import edu.stanford.spezi.core.bluetooth.api.BLEService
import edu.stanford.spezi.core.bluetooth.data.model.BLEServiceEvent
import edu.stanford.spezi.core.bluetooth.data.model.BLEServiceState
import edu.stanford.spezi.core.navigation.Navigator
import edu.stanford.spezi.core.testing.CoroutineTestRule
import edu.stanford.spezi.core.testing.runTestUnconfined
import edu.stanford.spezi.modules.measurements.MeasurementsRepository
import io.mockk.Runs
import io.mockk.coEvery
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
import java.text.NumberFormat
import java.time.Instant
import java.time.ZoneOffset
import java.util.Locale

class BluetoothViewModelTest {
    private val bleService: BLEService = mockk()
    private val uiStateMapper: BluetoothUiStateMapper = mockk()
    private val measurementsRepository = mockk<MeasurementsRepository>(relaxed = true)
    private val messageRepository = mockk<MessageRepository>(relaxed = true)

    private val bleServiceState = MutableStateFlow<BLEServiceState>(BLEServiceState.Idle)
    private val bleServiceEvents = MutableSharedFlow<BLEServiceEvent>()
    private val readyUiState: BluetoothUiState.Ready = mockk()
    private val bottomSheetEvents = mockk<BottomSheetEvents>(relaxed = true)
    private val navigator = mockk<Navigator>(relaxed = true)

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

    @Test
    fun `it should load weight successfully`() = runTestUnconfined {
        // Given
        val expectedWeight = getLocalizedWeight(70.0)
        val bodyWeightObservation = mockk<WeightRecord>().apply {
            every { weight.inGrams } returns 70000.0
            every { weight.inKilograms } returns 70.0
            every { weight.inPounds } returns 70.0
            every { time } returns Instant.now()
            every { zoneOffset } returns ZoneOffset.UTC
        }
        val flow = MutableStateFlow(Result.success(bodyWeightObservation))
        coEvery { measurementsRepository.observeWeightRecord() } returns flow

        // When
        createViewModel()

        // Then
        assertThat(bluetoothViewModel.uiState.value.weight.value).isEqualTo(expectedWeight)
    }

    @Test
    fun `it should load blood pressure successfully`() = runTestUnconfined {
        // Given
        val expectedBloodPressure = "120.0/80.0"
        val bloodPressureObservation = mockk<BloodPressureRecord>().apply {
            every { systolic } returns Pressure.millimetersOfMercury(120.0)
            every { diastolic } returns Pressure.millimetersOfMercury(80.0)
            every { time } returns Instant.now()
            every { zoneOffset } returns ZoneOffset.UTC
        }
        val flow = MutableStateFlow(Result.success(bloodPressureObservation))
        coEvery { measurementsRepository.observeBloodPressureRecord() } returns flow

        // When
        createViewModel()

        // Then
        assertThat(bluetoothViewModel.uiState.value.bloodPressure.value).isEqualTo(
            expectedBloodPressure
        )
    }

    @Test
    fun `it should load heart frequenz successfully`() = runTestUnconfined {
        // Given
        val expectedHeartRate = "70.0"
        val heartRateObservation = mockk<HeartRateRecord>().apply {
            every { samples } returns listOf(
                HeartRateRecord.Sample(
                    Instant.now(),
                    70
                )
            )
            every { startTime } returns Instant.now()
            every { endTime } returns Instant.now()
            every { startZoneOffset } returns ZoneOffset.UTC
            every { endZoneOffset } returns ZoneOffset.UTC
        }
        val flow = MutableStateFlow(Result.success(heartRateObservation))
        coEvery { measurementsRepository.observeHeartRateRecord() } returns flow

        // When
        createViewModel()

        // Then
        assertThat(bluetoothViewModel.uiState.value.heartRate.value).isEqualTo(expectedHeartRate)
    }

    private fun assertState(state: BluetoothUiState) {
        assertThat(bluetoothViewModel.uiState.value.bluetooth).isEqualTo(state)
    }

    private suspend fun assertEvent(event: BluetoothViewModel.Event) {
        assertThat(bluetoothViewModel.events.first()).isEqualTo(event)
    }

    private fun getLocalizedWeight(weightInKilograms: Double): String {
        val formatter = NumberFormat.getNumberInstance(Locale.getDefault())
        formatter.minimumFractionDigits = 2
        formatter.maximumFractionDigits = 2
        return formatter.format(weightInKilograms)
    }

    private fun createViewModel() {
        bluetoothViewModel = BluetoothViewModel(
            bleService = bleService,
            uiStateMapper = uiStateMapper,
            measurementsRepository = measurementsRepository,
            messageRepository = messageRepository,
            bottomSheetEvents = bottomSheetEvents,
            navigator = navigator,
            engageEducationRepository = mockk(),
            messageActionMapper = mockk(),
        )
    }
}
