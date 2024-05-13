package edu.stanford.spezikt.core.bluetooth.data.model

import android.bluetooth.BluetoothDevice

sealed interface BLEServiceEvent {
    data object BluetoothNotEnabled : BLEServiceEvent
    data class MissingPermissions(val permissions: List<String>): BLEServiceEvent
    data class GenericError(val cause: Throwable) : BLEServiceEvent
    data object ScanningStarted : BLEServiceEvent
    data class ScanningFailed(val errorCode: Int) : BLEServiceEvent
    data class Connected(val device: BluetoothDevice) : BLEServiceEvent
    data class Disconnected(val device: BluetoothDevice) : BLEServiceEvent
    data class MeasurementReceived(val device: BluetoothDevice, val measurement: Measurement) : BLEServiceEvent
}
