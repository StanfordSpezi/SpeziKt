package edu.stanford.bdh.engagehf.bluetooth.data.models

import edu.stanford.bdh.engagehf.R
import edu.stanford.spezi.ui.StringResource

sealed interface BluetoothUiState {
    data class Idle(
        val description: StringResource = StringResource(R.string.bluetooth_not_enabled_description),
        val settingsAction: Action.Settings? = null,
    ) : BluetoothUiState

    data class Ready(val header: StringResource?, val devices: List<DeviceUiModel>) : BluetoothUiState
}
