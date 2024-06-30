package edu.stanford.bdh.engagehf.bluetooth

import androidx.health.connect.client.records.BloodPressureRecord
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.records.WeightRecord
import androidx.health.connect.client.units.Mass
import androidx.health.connect.client.units.Pressure
import com.google.common.truth.Truth.assertThat
import edu.stanford.bdh.engagehf.bluetooth.data.mapper.BluetoothUiStateMapper
import edu.stanford.bdh.engagehf.bluetooth.data.mapper.DefaultMeasurementToRecordMapper
import edu.stanford.bdh.engagehf.bluetooth.data.mapper.MeasurementToRecordMapper
import edu.stanford.bdh.engagehf.bluetooth.data.models.BluetoothUiState
import edu.stanford.bdh.engagehf.bluetooth.data.repository.ObservationRepository
import edu.stanford.healthconnectonfhir.RecordToObservationMapper
import edu.stanford.healthconnectonfhir.RecordToObservationMapperImpl
import edu.stanford.spezi.core.bluetooth.api.BLEService
import edu.stanford.spezi.core.bluetooth.data.model.BLEServiceEvent
import edu.stanford.spezi.core.bluetooth.data.model.BLEServiceState
import edu.stanford.spezi.core.testing.CoroutineTestRule
import edu.stanford.spezi.core.testing.runTestUnconfined
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
import java.time.ZonedDateTime

class BluetoothViewModelTest {
    private val bleService: BLEService = mockk()
    private val uiStateMapper: BluetoothUiStateMapper = mockk()
    private val measurementToRecordMapper: MeasurementToRecordMapper =
        DefaultMeasurementToRecordMapper()
    private val observationRepository = mockk<ObservationRepository>()
    private val recordToObservation: RecordToObservationMapper = RecordToObservationMapperImpl()

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
        coEvery { observationRepository.getLatestBloodPressureObservation() } returns Result.success(
            createBloodPressureRecord(120.0, 80.0)
        )
        coEvery { observationRepository.getLatestBodyWeightObservation() } returns Result.success(
            createWeightRecord(70.0)
        )
        coEvery { observationRepository.getLatestHeartRateObservation() } returns Result.success(
            createHeartRateRecord(60L)
        )
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
        assertThat(bluetoothViewModel.uiState.value).isEqualTo(state)
    }

    private suspend fun assertEvent(event: BluetoothViewModel.Event) {
        assertThat(bluetoothViewModel.events.first()).isEqualTo(event)
    }

    private fun createViewModel() {
        bluetoothViewModel = BluetoothViewModel(
            bleService = bleService,
            uiStateMapper = uiStateMapper,
            measurementToRecordMapper = measurementToRecordMapper,
            observationRepository = observationRepository,
            recordToObservation = recordToObservation,
        )
    }

    private fun createBloodPressureRecord(
        systolic: Double,
        diastolic: Double,
    ): BloodPressureRecord {
        return BloodPressureRecord(
            time = ZonedDateTime.now().toInstant(),
            zoneOffset = ZonedDateTime.now().offset,
            systolic = Pressure.millimetersOfMercury(systolic),
            diastolic = Pressure.millimetersOfMercury(diastolic)
        )
    }

    private fun createWeightRecord(weight: Double): WeightRecord {
        return WeightRecord(
            time = ZonedDateTime.now().toInstant(),
            zoneOffset = ZonedDateTime.now().offset,
            weight = Mass.kilograms(weight)
        )
    }

    private fun createHeartRateRecord(heartRate: Long): HeartRateRecord {
        return HeartRateRecord(
            startTime = ZonedDateTime.now().toInstant(),
            endTime = ZonedDateTime.now().toInstant(),
            startZoneOffset = ZonedDateTime.now().offset,
            endZoneOffset = ZonedDateTime.now().offset,
            samples = listOf(
                HeartRateRecord.Sample(
                    time = ZonedDateTime.now().toInstant(),
                    beatsPerMinute = heartRate
                )
            )
        )
    }
}
