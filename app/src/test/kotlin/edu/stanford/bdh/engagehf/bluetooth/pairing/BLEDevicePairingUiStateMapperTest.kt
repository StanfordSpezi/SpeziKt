package edu.stanford.bdh.engagehf.bluetooth.pairing

import android.bluetooth.BluetoothDevice
import com.google.common.truth.Truth.assertThat
import edu.stanford.bdh.engagehf.R
import io.mockk.every
import io.mockk.mockk
import org.junit.Test

class BLEDevicePairingUiStateMapperTest {
    private val mapper = BLEDevicePairingUiStateMapper()

    @Test
    fun `it should map the correct initial state`() {
        // when
        val state = mapper.mapInitialState()

        // then
        assertThat(state).isEqualTo(
            BLEDevicePairingViewModel.UiState.Discovering(
                title = StringResource(R.string.ble_device_discovering_title),
                subtitle = StringResource(R.string.ble_device_discovering_subtitle),
            )
        )
        assertThat(state.action).isNull()
    }

    @Test
    fun `it should map the correct device found state`() {
        // given
        val deviceName = "some device name"
        val device: BluetoothDevice = mockk {
            every { name } returns deviceName
        }

        // when
        val state = mapper.mapDeviceFoundState(device)

        // then
        assertThat(state).isEqualTo(
            BLEDevicePairingViewModel.UiState.DeviceFound(
                title = StringResource(R.string.ble_device_found_title),
                subtitle = StringResource(R.string.ble_device_found_subtitle, deviceName),
            )
        )
        assertThat(state.action).isEqualTo(BLEDevicePairingViewModel.Action.Pair)
        assertThat(state.action.title)
            .isEqualTo(StringResource(R.string.ble_device_pair_action_title))
    }

    @Test
    fun `it should map the correct device paired state`() {
        // given
        val deviceName = "some device name"
        val device: BluetoothDevice = mockk {
            every { name } returns deviceName
        }

        // when
        val state = mapper.mapDevicePairedState(device)

        // then
        assertThat(state).isEqualTo(
            BLEDevicePairingViewModel.UiState.DevicePaired(
                title = StringResource(R.string.ble_device_paired_title),
                subtitle = StringResource(R.string.ble_device_paired_subtitle, deviceName),
            )
        )
        assertThat(state.action).isEqualTo(BLEDevicePairingViewModel.Action.Done)
        assertThat(state.action.title)
            .isEqualTo(StringResource(R.string.ble_device_pair_done_action_title))
    }

    @Test
    fun `it should map the correct error state`() {
        // when
        val state = mapper.mapErrorState()

        // then
        assertThat(state).isEqualTo(
            BLEDevicePairingViewModel.UiState.Error(
                title = StringResource(R.string.ble_device_error_title),
                subtitle = StringResource(R.string.ble_device_error_subtitle),
            )
        )
        assertThat(state.action).isEqualTo(BLEDevicePairingViewModel.Action.Done)
    }
}
