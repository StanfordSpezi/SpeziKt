package edu.stanford.bdh.engagehf.bluetooth.spezi.model.actions

import edu.stanford.bdh.engagehf.bluetooth.spezi.core.BluetoothPeripheral
import edu.stanford.bdh.engagehf.bluetooth.spezi.model.properties.BluetoothPeripheralAction

data class ReadRSSIAction(val peripheral: BluetoothPeripheral) : BluetoothPeripheralAction {
    suspend fun invoke(): Int = TODO()
}
