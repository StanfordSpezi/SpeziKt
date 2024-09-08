package edu.stanford.bdh.engagehf.bluetooth.data.models

import edu.stanford.bdh.engagehf.messages.Message

data class UiState(
    val bloodPressure: VitalDisplayData = VitalDisplayData(
        title = "Blood Pressure",
    ),
    val heartRate: VitalDisplayData = VitalDisplayData(
        title = "Heart Rate",
    ),
    val weight: VitalDisplayData = VitalDisplayData(
        title = "Weight",
    ),
    val messages: List<Message> = emptyList(),
    val bluetooth: BluetoothUiState = BluetoothUiState.Idle(),
    val measurementDialog: MeasurementDialogUiState = MeasurementDialogUiState(),
)
