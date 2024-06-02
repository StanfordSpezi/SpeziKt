package edu.stanford.spezi.core.bluetooth.data.model

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
     * Represents the scanning state of the BLE service.
     *
     * @property sessions The list of BLE device sessions being scanned.
     */
    data class Scanning(val sessions: List<BLEDeviceSession>) : BLEServiceState
}
