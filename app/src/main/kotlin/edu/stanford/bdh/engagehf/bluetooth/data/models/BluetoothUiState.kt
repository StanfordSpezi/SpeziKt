package edu.stanford.bdh.engagehf.bluetooth.data.models

import edu.stanford.spezi.core.bluetooth.data.model.Measurement

sealed interface BluetoothUiState {
    // Initial states
    data object Idle : BluetoothUiState
    data object Scanning : BluetoothUiState

    // Ready; scanning or already connected to devices and receiving measurements
    data class Ready(val header: String, val devices: List<DeviceUiModel>) : BluetoothUiState

    // Generic error
    data class Error(val message: String) : BluetoothUiState
}

data class MeasurementDialogUiState(
    val measurement: Measurement? = null,
    val isVisible: Boolean = false,
)

sealed interface Action {
    data class ConfirmMeasurement(val measurement: Measurement) : Action
    data object DismissDialog : Action
}
