package edu.stanford.spezi.core.bluetooth.data.model

import android.bluetooth.BluetoothDevice

/**
 * Represents the state of the Bluetooth Low Energy (BLE) service.
 *
 * This sealed interface defines various states that the BLE service can be in.
 */
sealed interface BLEServiceState {

    /**
     * Represents the idle state of the BLE service.
     */
    data object Idle : BLEServiceState

    /**
     * Represents an event indicating that Bluetooth is not enabled.
     */
    data object BluetoothNotEnabled : BLEServiceState

    /**
     * Represents an event indicating missing permissions.
     *
     * @property permissions The list of permissions that are missing.
     */
    data class MissingPermissions(val permissions: List<String>) : BLEServiceState

    /**
     * Represents the scanning state of the BLE service.
     *
     * @property pairedDevices The list of paired devices.
     */
    data class Scanning(val pairedDevices: List<BluetoothDevice>) : BLEServiceState
}
