package edu.stanford.spezikt.core.bluetooth.domain

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothProfile
import android.content.Context
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.qualifiers.ApplicationContext
import edu.stanford.spezi.utils.UUID
import edu.stanford.spezikt.core.bluetooth.data.mapper.MeasurementMapper
import edu.stanford.spezikt.core.bluetooth.data.model.BLEServiceEvent
import edu.stanford.spezikt.coroutines.di.Dispatching
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean

@SuppressLint("MissingPermission")
internal class BLEDeviceConnector @AssistedInject constructor(
    @Assisted private val device: BluetoothDevice,
    private val measurementMapper: MeasurementMapper,
    @Dispatching.IO private val scope: CoroutineScope,
    @ApplicationContext private val context: Context,
) {

    private var bluetoothGatt: BluetoothGatt? = null
    private val _events = MutableSharedFlow<BLEServiceEvent>(replay = 1, extraBufferCapacity = 1)
    private val isDestroyed = AtomicBoolean(false)

    val events = _events.asSharedFlow()

    private val gattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            when (newState) {
                BluetoothProfile.STATE_CONNECTED -> {
                    gatt.discoverServices()
                    emit(event = BLEServiceEvent.Connected(device))
                }
                BluetoothProfile.STATE_DISCONNECTED -> {
                    emit(event = BLEServiceEvent.Disconnected(device))
                }
            }
        }

        override fun onCharacteristicChanged(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic, value: ByteArray) {
            scope.launch {
                measurementMapper.map(characteristic = characteristic, data = value)?.let {
                    emit(event = BLEServiceEvent.MeasurementReceived(device = device, measurement = it))
                }
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
            gatt?.services?.forEach { service ->
                service.characteristics.forEach { characteristic ->
                    if (measurementMapper.recognises(characteristic)) {
                        gatt.setCharacteristicNotification(characteristic, true)
                        val descriptor = characteristic.getDescriptor(DESCRIPTOR_UUID)
                        descriptor.value = BluetoothGattDescriptor.ENABLE_INDICATION_VALUE
                        gatt.writeDescriptor(descriptor)
                    }
                }
            }
        }
    }

    fun connect() {
        val currentGatt = bluetoothGatt
        if (currentGatt != null || isDestroyed.get()) return
        bluetoothGatt = device.connectGatt(context, false, gattCallback)
    }

    fun disconnect() {
        if (isDestroyed.getAndSet(true).not()) {
            bluetoothGatt?.disconnect() ?: emit(event = BLEServiceEvent.Disconnected(device = device))
            bluetoothGatt = null
        }
    }

    private fun emit(event: BLEServiceEvent) {
        if (isDestroyed.get()) return
        scope.launch { _events.emit(event) }
    }

    @AssistedFactory
    interface Factory {
        fun create(device: BluetoothDevice): BLEDeviceConnector
    }

    private companion object {
        val DESCRIPTOR_UUID = UUID("00002902-0000-1000-8000-00805f9b34fb")
    }
}