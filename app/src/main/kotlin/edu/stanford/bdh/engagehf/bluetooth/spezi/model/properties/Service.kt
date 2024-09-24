package edu.stanford.bdh.engagehf.bluetooth.spezi.model.properties

import edu.stanford.bdh.engagehf.bluetooth.spezi.model.BluetoothDevice
import edu.stanford.bdh.engagehf.bluetooth.spezi.model.BluetoothService
import kotlin.reflect.KProperty

data class Service<S: BluetoothService>(val service: S) {
    operator fun getValue(thisRef: BluetoothDevice, property: KProperty<*>): S {
        return service
    }
}