package edu.stanford.spezi.core.bluetooth.data.model

import android.bluetooth.BluetoothDevice

/**
 * Represents a session with a Bluetooth Low Energy (BLE) device.
 *
 * This data class encapsulates information the BLE device and the measurements associated with it.
 *
 * @property device The Bluetooth device associated with the session.
 * @property measurements The list of measurements received from the device during the session.
 */
data class BLEDeviceSession(
    val device: BluetoothDevice,
    val measurements: List<Measurement>
)
