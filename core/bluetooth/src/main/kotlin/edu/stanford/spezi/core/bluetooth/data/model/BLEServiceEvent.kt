package edu.stanford.spezi.core.bluetooth.data.model

import android.bluetooth.BluetoothDevice

/**
 * Represents events emitted by the Bluetooth Low Energy (BLE) service.
 */
sealed interface BLEServiceEvent {

    /**
     * Represents an event indicating that Bluetooth is not enabled.
     */
    data object BluetoothNotEnabled : BLEServiceEvent

    /**
     * Represents an event indicating missing permissions.
     *
     * @property permissions The list of permissions that are missing.
     */
    data class MissingPermissions(val permissions: List<String>) : BLEServiceEvent

    /**
     * Represents a generic error event.
     *
     * @property cause The cause of the error.
     */
    data class GenericError(val cause: Throwable) : BLEServiceEvent

    /**
     * Represents an event indicating that scanning has started.
     */
    data object ScanningStarted : BLEServiceEvent

    /**
     * Represents an event indicating that scanning has failed.
     *
     * @property errorCode The error code associated with the scanning failure.
     */
    data class ScanningFailed(val errorCode: Int) : BLEServiceEvent

    /**
     * Represents an event indicating that connection to a device has been established.
     *
     * @property device The Bluetooth device that is connected.
     */
    data class Connected(val device: BluetoothDevice) : BLEServiceEvent

    /**
     * Represents an event indicating that a device is disconnected.
     *
     * @property device The Bluetooth device that is disconnected.
     */
    data class Disconnected(val device: BluetoothDevice) : BLEServiceEvent

    /**
     * Represents an event indicating that a measurement is received.
     *
     * @property device The Bluetooth device from which the measurement is received.
     * @property measurement The measurement received from the device.
     */
    data class MeasurementReceived(val device: BluetoothDevice, val measurement: Measurement) : BLEServiceEvent
}
