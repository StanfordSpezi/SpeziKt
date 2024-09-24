package edu.stanford.bdh.engagehf.bluetooth.spezi

import edu.stanford.bdh.engagehf.bluetooth.spezi.core.BluetoothPeripheral
import edu.stanford.bdh.engagehf.bluetooth.spezi.model.BluetoothDevice
import edu.stanford.bdh.engagehf.bluetooth.spezi.model.properties.DeviceAction
import edu.stanford.bdh.engagehf.bluetooth.spezi.model.properties.DeviceState
import edu.stanford.bdh.engagehf.bluetooth.spezi.model.properties.Service

class MockDevice: BluetoothDevice {
    val id by DeviceState(BluetoothPeripheral::id)
    val name by DeviceState(BluetoothPeripheral::name)
    val state by DeviceState(BluetoothPeripheral::state)
    val rssi by DeviceState(BluetoothPeripheral::rssi)
    val nearby by DeviceState(BluetoothPeripheral::nearby)
    val lastActivity by DeviceState(BluetoothPeripheral::lastActivity)

    val connect by DeviceAction.connect

    val deviceInformation by Service(DeviceInformationService())
}