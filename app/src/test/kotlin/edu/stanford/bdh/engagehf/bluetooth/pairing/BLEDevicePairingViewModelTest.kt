package edu.stanford.bdh.engagehf.bluetooth.pairing

import android.bluetooth.BluetoothDevice
import com.google.common.truth.Truth.assertThat
import edu.stanford.bdh.engagehf.bluetooth.component.AppScreenEvents
import edu.stanford.bdh.engagehf.bluetooth.service.EngageBLEService
import edu.stanford.bdh.engagehf.bluetooth.service.EngageBLEServiceEvent
import edu.stanford.spezi.core.testing.CoroutineTestRule
import edu.stanford.spezi.core.testing.runTestUnconfined
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.MutableSharedFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class BLEDevicePairingViewModelTest {
    @get:Rule
    val coroutineTestRule = CoroutineTestRule()

    private val engageBLEService: EngageBLEService = mockk(relaxed = true)
    private val uiStateMapper: BLEDevicePairingUiStateMapper = mockk(relaxed = true)
    private val appScreenEvents: AppScreenEvents = mockk(relaxed = true)
    private val screenEventsFlow = MutableSharedFlow<AppScreenEvents.Event>()
    private val serviceFlow = MutableSharedFlow<EngageBLEServiceEvent>()
    private val discovering: BLEDevicePairingViewModel.UiState.Discovering = mockk()
    private val deviceFound = BLEDevicePairingViewModel.UiState.DeviceFound(
        title = StringResource(""),
        subtitle = StringResource("")
    )
    private val devicePaired: BLEDevicePairingViewModel.UiState.DevicePaired = mockk()
    private val error: BLEDevicePairingViewModel.UiState.Error = mockk()
    private val bluetoothDevice: BluetoothDevice = mockk {
        every { address } returns "device-address"
    }

    private val viewModel by lazy {
        BLEDevicePairingViewModel(
            bleService = engageBLEService,
            uiStateMapper = uiStateMapper,
            appScreenEvents = appScreenEvents
        )
    }

    @Before
    fun setup() {
        every { engageBLEService.events } returns serviceFlow
        every { appScreenEvents.events } returns screenEventsFlow
        with(uiStateMapper) {
            every { mapInitialState() } returns discovering
            every { mapDevicePairedState(bluetoothDevice) } returns devicePaired
            every { mapDeviceFoundState(bluetoothDevice) } returns deviceFound
            every { mapErrorState() } returns error
        }
    }

    @Test
    fun `it should have correct initialization`() {
        // given
        val state = viewModel.uiState.value

        // then
        assertThat(state).isEqualTo(discovering)
        verify { uiStateMapper.mapInitialState() }
        verify { engageBLEService.events }
        verify { appScreenEvents.events }
    }

    @Test
    fun `it should react correctly on device discovered event`() = runTestUnconfined {
        // given
        viewModel

        // when
        serviceFlow.emit(EngageBLEServiceEvent.DeviceDiscovered(bluetoothDevice))
        val state = viewModel.uiState.value

        // then
        assertThat(state).isEqualTo(deviceFound)
        verify { uiStateMapper.mapDeviceFoundState(bluetoothDevice) }
    }

    @Test
    fun `it should handle screen events correctly`() = runTestUnconfined {
        // given
        viewModel

        // when
        screenEventsFlow.emit(AppScreenEvents.Event.CloseBottomSheet)
        screenEventsFlow.emit(AppScreenEvents.Event.BLEDevicePairingBottomSheet)
        val state = viewModel.uiState.value

        // then
        assertThat(state).isEqualTo(discovering)
        verify(exactly = 2) { engageBLEService.events }
    }

    @Test
    fun `it should handle done action correctly`() {
        // when
        viewModel.onAction(BLEDevicePairingViewModel.Action.Done)

        // then
        verify { appScreenEvents.emit(AppScreenEvents.Event.CloseBottomSheet) }
    }

    @Test
    fun `it should handle pair event correctly`() = runTestUnconfined {
        // when
        viewModel
        serviceFlow.emit(EngageBLEServiceEvent.DeviceDiscovered(bluetoothDevice))
        viewModel.onAction(BLEDevicePairingViewModel.Action.Pair)
        serviceFlow.emit(EngageBLEServiceEvent.DevicePaired(bluetoothDevice))

        // then
        verify { engageBLEService.pair(bluetoothDevice) }
        assertThat(viewModel.uiState.value).isEqualTo(devicePaired)
    }
}
