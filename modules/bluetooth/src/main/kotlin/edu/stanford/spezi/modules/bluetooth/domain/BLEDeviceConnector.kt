package edu.stanford.spezi.modules.bluetooth.domain

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothProfile
import android.content.Context
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.qualifiers.ApplicationContext
import edu.stanford.spezi.core.coroutines.Dispatching
import edu.stanford.spezi.modules.bluetooth.model.BLEDevice
import edu.stanford.spezi.modules.bluetooth.model.BLEServiceEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Component responsible for establishing and managing a connection to a BLE (Bluetooth Low Energy) device.
 *
 * Note that this class, cannot be injected directly as it requires an `@Assisted` [BluetoothDevice] property. In order
 * to create instances of this class, please inject [BLEDeviceConnector.Factory] and use `factory.create(device)` with
 * the [BluetoothDevice] that this component should manage.
 *
 * @property device The Bluetooth device to connect to.
 * @property scope The coroutine scope used for launching connection events and mapping measurements.
 * @property context The application context.
 */
@Suppress("MissingPermission")
internal class BLEDeviceConnector @AssistedInject constructor(
    @Assisted private val device: BluetoothDevice,
    @Dispatching.IO private val scope: CoroutineScope,
    @ApplicationContext private val context: Context,
) {
    private var bluetoothGatt: BluetoothGatt? = null
    private val _events = MutableSharedFlow<BLEServiceEvent>(replay = 1, extraBufferCapacity = 1)
    private val isDestroyed = AtomicBoolean(false)

    /**
     * Flow of events emitted by the BLE device connector.
     */
    val events = _events.asSharedFlow()

    private val gattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            when (newState) {
                BluetoothProfile.STATE_CONNECTED -> {
                    gatt.discoverServices()
                    emit(event = BLEServiceEvent.Connected(bleDevice(connected = true)))
                }

                BluetoothProfile.STATE_DISCONNECTED -> {
                    bluetoothGatt = null
                    disconnect()
                }
            }
        }

        override fun onCharacteristicChanged(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            value: ByteArray,
        ) {
            emit(
                event = BLEServiceEvent.CharacteristicChanged(
                    device = bleDevice(connected = true),
                    gatt = gatt,
                    characteristic = characteristic,
                    value = value,
                )
            )
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
            gatt ?: return
            emit(
                event = BLEServiceEvent.ServiceDiscovered(
                    device = device,
                    gatt = gatt,
                    status = status,
                )
            )
        }
    }

    /**
     * Establishes a connection to the BLE device.
     *
     * If a connection already exists or the connector is destroyed, this method does nothing.
     */
    fun connect() {
        val currentGatt = bluetoothGatt
        if (currentGatt != null || isDestroyed.get()) return
        device.createBond()
        bluetoothGatt = device.connectGatt(context, false, gattCallback)
    }

    /**
     * Disconnects from the BLE device.
     *
     * If the connector is already destroyed, this method does nothing.
     */
    fun disconnect() {
        if (isDestroyed.getAndSet(true).not()) {
            val currentGatt = bluetoothGatt
            if (currentGatt != null) {
                currentGatt.disconnect()
            } else {
                emit(event = BLEServiceEvent.Disconnected(bleDevice(false)))
            }
        }
    }

    private fun bleDevice(connected: Boolean) = BLEDevice(
        address = device.address,
        name = device.name,
        connected = connected
    )

    private fun emit(event: BLEServiceEvent) {
        scope.launch { _events.emit(event) }
    }

    /**
     * Factory interface for creating instances of [BLEDeviceConnector].
     */
    @AssistedFactory
    interface Factory {
        /**
         * Creates a new instance of [BLEDeviceConnector].
         * @param device The Bluetooth device to connect to.
         * @return A new instance of [BLEDeviceConnector].
         */
        fun create(device: BluetoothDevice): BLEDeviceConnector
    }
}
