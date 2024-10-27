package edu.stanford.bdh.engagehf.bluetooth.spezi.model.properties

import edu.stanford.bdh.engagehf.bluetooth.spezi.Bluetooth
import edu.stanford.bdh.engagehf.bluetooth.spezi.BluetoothError
import edu.stanford.bdh.engagehf.bluetooth.spezi.core.BluetoothPeripheral
import edu.stanford.bdh.engagehf.bluetooth.spezi.model.BluetoothDevice
import kotlin.reflect.KProperty
import kotlin.reflect.KProperty1

data class DeviceState<Value>(val property: KProperty1<BluetoothPeripheral, Value>) {
    private var peripheral: BluetoothPeripheral? = null

    fun inject(bluetooth: Bluetooth, peripheral: BluetoothPeripheral) {
        this.peripheral = peripheral
    }

    operator fun getValue(thisRef: BluetoothDevice, property: KProperty<*>): Value {
        return this.peripheral?.let {
            this.property.get(it)
        } ?: throw BluetoothError("Not found")
    }
}
