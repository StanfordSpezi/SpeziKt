package edu.stanford.bdh.engagehf.bluetooth.spezi.model.actions

import edu.stanford.bdh.engagehf.bluetooth.spezi.core.BluetoothPeripheral
import edu.stanford.bdh.engagehf.bluetooth.spezi.model.properties.BluetoothPeripheralAction

// TODO: Implement and check if we can really not get rid of injecting closures
data class BluetoothConnectAction(val peripheral: BluetoothPeripheral) : BluetoothPeripheralAction {
    suspend fun invoke() {
        peripheral.connect()
    }
}
