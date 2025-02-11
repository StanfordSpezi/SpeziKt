package edu.stanford.bdh.engagehf.bluetooth.data.models

import edu.stanford.bdh.engagehf.R

sealed interface BluetoothUiState {
    data class Idle(
        val description: Int = R.string.bluetooth_not_enabled_description,
        val settingsAction: Action.Settings? = null,
    ) : BluetoothUiState

    data class Ready(val header: Int?, val devices: List<DeviceUiModel>) : BluetoothUiState
}
