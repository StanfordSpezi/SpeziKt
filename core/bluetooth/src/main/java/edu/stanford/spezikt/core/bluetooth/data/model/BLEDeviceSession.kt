package edu.stanford.spezikt.core.bluetooth.data.model

import android.bluetooth.BluetoothDevice

data class BLEDeviceSession(
    val device: BluetoothDevice,
    val measurements: List<Measurement>
)