package edu.stanford.bdh.engagehf.bluetooth.spezi.core.model

import edu.stanford.bdh.engagehf.bluetooth.spezi.core.BluetoothPeripheral
import edu.stanford.bdh.engagehf.bluetooth.spezi.utils.BTUUID

// TODO: What's up with this observable thing there?
class BluetoothManagerStorage {
    val connectedDevices = mutableSetOf<BTUUID>()
    val retrievedPeripherals = mutableMapOf<BTUUID, BluetoothPeripheral>()
    var isScanning: Boolean = false

    val hasConnectedDevices: Boolean
        get() = connectedDevices.isNotEmpty()

    var state: BluetoothState = BluetoothState.UNKNOWN
        private set

    fun update(state: BluetoothState) {
        this.state = state
    }
}