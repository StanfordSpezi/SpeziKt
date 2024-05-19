package edu.stanford.spezikt.bluetooth.data.models

sealed interface BluetoothUiState {
    // Initial states
    data object Idle : BluetoothUiState
    data object Scanning : BluetoothUiState

    // Ready; scanning or already connected to devices and receiving measurements
    data class Ready(val header: String, val devices: List<DeviceUiModel>) : BluetoothUiState

    // Generic error
    data class Error(val message: String) : BluetoothUiState
}
