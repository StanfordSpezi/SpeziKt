package edu.stanford.spezikt.core.bluetooth

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback

class DeviceConnector(
    private val device: BluetoothDevice,
    private val converters: Map<BluetoothUUID, DataConverter<*>>,
    private val listener: CustomGattCallback.DataListener,
    private val connectionListener: DeviceConnectionListener
) {

    private var bluetoothGatt: BluetoothGatt? = null

    private val gattCallback: BluetoothGattCallback by lazy {
        CustomGattCallback(converters, listener, this)
    }

    @SuppressLint("MissingPermission")
    fun connect() {
        bluetoothGatt = device.connectGatt(null, false, gattCallback)
    }

    @SuppressLint("MissingPermission")
    fun disconnect() {
        bluetoothGatt?.disconnect()
        connectionListener.onConnectionClosed(device)
    }

    @SuppressLint("MissingPermission")
    fun close() {
        bluetoothGatt?.disconnect()
        bluetoothGatt = null
        connectionListener.onConnectionClosed(device)
    }

    fun connectionLost() {
        connectionListener.onConnectionLost(device)
    }
}

interface DeviceConnectionListener {
    fun onConnectionClosed(device: BluetoothDevice)
    fun onConnectionLost(device: BluetoothDevice)
}
