package edu.stanford.bdh.engagehf.bluetooth.spezi.model.properties

import edu.stanford.bdh.engagehf.bluetooth.spezi.model.BluetoothDevice
import edu.stanford.bdh.engagehf.bluetooth.spezi.core.BluetoothPeripheral
import edu.stanford.bdh.engagehf.bluetooth.spezi.model.actions.BluetoothConnectAction
import edu.stanford.bdh.engagehf.bluetooth.spezi.model.actions.BluetoothDisconnectAction
import edu.stanford.bdh.engagehf.bluetooth.spezi.model.actions.ReadRSSIAction
import kotlin.reflect.KProperty

data class DeviceAction<Action: BluetoothPeripheralAction>(val createAction: (BluetoothPeripheral) -> Action) {
    class Storage(var peripheral: BluetoothPeripheral? = null)

    private val storage: Storage = Storage()

    operator fun <Device: BluetoothDevice> getValue(
        thisRef: Device,
        property: KProperty<*>
    ): Action {
        return storage.peripheral?.let {
            createAction(it)
        } ?: throw Error()
    }

    fun inject(peripheral: BluetoothPeripheral?) {
        storage.peripheral = peripheral
    }

    companion object {
        val connect get() = DeviceAction { BluetoothConnectAction(it) }
        val disconnect get() = DeviceAction { BluetoothDisconnectAction(it) }
        val readRSSI get() = DeviceAction { ReadRSSIAction(it) }
    }
}