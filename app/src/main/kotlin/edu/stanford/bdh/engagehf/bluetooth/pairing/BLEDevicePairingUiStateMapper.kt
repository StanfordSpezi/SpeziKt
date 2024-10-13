package edu.stanford.bdh.engagehf.bluetooth.pairing

import android.bluetooth.BluetoothDevice
import edu.stanford.bdh.engagehf.R
import edu.stanford.spezi.core.design.component.StringResource
import javax.inject.Inject

@Suppress("MissingPermission")
class BLEDevicePairingUiStateMapper @Inject constructor() {

    fun mapInitialState() =
        BLEDevicePairingViewModel.UiState.Discovering(
            title = StringResource(R.string.ble_device_discovering_title),
            subtitle = StringResource(R.string.ble_device_discovering_subtitle),
        )

    fun mapDeviceFoundState(bluetoothDevice: BluetoothDevice) =
        BLEDevicePairingViewModel.UiState.DeviceFound(
            title = StringResource(R.string.ble_device_found_title),
            subtitle = StringResource(
                R.string.ble_device_found_subtitle,
                bluetoothDevice.name
            ),
        )

    fun mapDevicePairedState(bluetoothDevice: BluetoothDevice) =
        BLEDevicePairingViewModel.UiState.DevicePaired(
            title = StringResource(R.string.ble_device_paired_title),
            subtitle = StringResource(
                R.string.ble_device_paired_subtitle,
                bluetoothDevice.name,
            ),
        )

    fun mapErrorState() = BLEDevicePairingViewModel.UiState.Error(
        title = StringResource(R.string.ble_device_error_title),
        subtitle = StringResource(R.string.ble_device_error_subtitle),
    )
}
