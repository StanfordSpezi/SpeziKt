package edu.stanford.spezi.modules.bluetooth.model

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic

/**
 * Represents events emitted by the Bluetooth Low Energy (BLE) service.
 */
sealed interface BLEServiceEvent {

    /**
     * Represents a generic error event.
     *
     * @property cause The cause of the error.
     */
    data class GenericError(val cause: Throwable) : BLEServiceEvent

    /**
     * Represents an event indicating that scanning has failed.
     *
     * @property errorCode The error code associated with the scanning failure.
     */
    data class ScanningFailed(val errorCode: Int) : BLEServiceEvent

    /**
     * Represents an event indicating that connection to a device has been established.
     *
     * @property device The Bluetooth device that is connected.
     */
    data class Connected(val device: BLEDevice) : BLEServiceEvent

    /**
     * Represents an event indicating that a device is disconnected.
     *
     * @property device The Bluetooth device that is disconnected.
     */
    data class Disconnected(val device: BLEDevice) : BLEServiceEvent

    /**
     * Represents an event indicating that a device has been discovered
     */
    data class DeviceDiscovered(val device: BluetoothDevice) : BLEServiceEvent

    /**
     * Event indicating that a BLE device was paired.
     * @property device BLE device
     */
    data class DevicePaired(val device: BluetoothDevice) : BLEServiceEvent

    /**
     * Event indicating that a BLE device was paired.
     * @property device BLE device
     */
    data class DeviceUnpaired(val device: BluetoothDevice) : BLEServiceEvent

    /**
     * Represents a characteristic changed event
     *
     * @property device The Bluetooth device for which the characteristic changed
     * @property gatt the bluetooth gatt profile
     * @property characteristic Changed characteristic
     * @property value Changed data
     */
    data class CharacteristicChanged(
        val device: BLEDevice,
        val gatt: BluetoothGatt,
        val characteristic: BluetoothGattCharacteristic,
        val value: ByteArray,
    ) : BLEServiceEvent {
        override fun equals(other: Any?): Boolean {
            if (other !is CharacteristicChanged) return false

            return this === other ||
                (device == other.device &&
                    gatt == other.gatt &&
                    characteristic == other.characteristic &&
                    value.contentEquals(other.value))
        }

        override fun hashCode(): Int {
            var result = device.hashCode()
            result = 31 * result + gatt.hashCode()
            result = 31 * result + characteristic.hashCode()
            result = 31 * result + value.contentHashCode()
            return result
        }
    }

    data class ServiceDiscovered(
        val device: BluetoothDevice,
        val gatt: BluetoothGatt,
        val status: Int,
    ) : BLEServiceEvent
}
