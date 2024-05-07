package edu.stanford.spezikt.core.bluetooth

import android.annotation.SuppressLint
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothProfile
import android.util.Log
import java.util.UUID

class CustomGattCallback(
    private val converters: Map<BluetoothUUID, DataConverter<*>>,
    private val listener: DataListener,
    private val deviceConnector: DeviceConnector
) :
    BluetoothGattCallback() {

    companion object {
        val DESCRIPTOR_UUID: UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")
    }


    @SuppressLint("MissingPermission")
    override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
        if (newState == BluetoothProfile.STATE_CONNECTED) {
            Log.i("Bluetooth", "Connected to GATT server")
            gatt.discoverServices()
        } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
            Log.i("Bluetooth", "Disconnected from GATT server")
            deviceConnector.connectionLost()
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onCharacteristicChanged(
        gatt: BluetoothGatt?,
        characteristic: BluetoothGattCharacteristic?
    ) {
        Log.i("Bluetooth", "Characteristic changed")
        characteristic?.let {
            converters[BluetoothUUID(it.service.uuid, it.uuid)]?.let { converter ->
                val convertedData = converter.convert(it.value)
                listener.onDataReceived(convertedData)
            }
        }
        Log.i("Bluetooth Data", characteristic?.value.toString())
    }

    @SuppressLint("MissingPermission")
    override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
        Log.i("Bluetooth", "Services discovered")
        gatt.services?.forEach { service ->
            service.characteristics.forEach { characteristic ->
                converters[BluetoothUUID(service.uuid, characteristic.uuid)]?.let {
                    gatt.setCharacteristicNotification(characteristic, true)
                    val descriptor =
                        characteristic.getDescriptor(DESCRIPTOR_UUID)
                    descriptor.value = BluetoothGattDescriptor.ENABLE_INDICATION_VALUE
                    gatt.writeDescriptor(descriptor)
                }
            }
        }
    }

    interface DataListener {
        fun <T> onDataReceived(data: T)
    }
}

data class BluetoothUUID(val serviceUUID: UUID, val characteristicUUID: UUID)