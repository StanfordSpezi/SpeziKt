package edu.stanford.bdh.engagehf.bluetooth.spezi

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import edu.stanford.bdh.engagehf.bluetooth.spezi.configuration.DeviceDiscoveryDescriptor
import edu.stanford.bdh.engagehf.bluetooth.spezi.core.BluetoothManager
import edu.stanford.bdh.engagehf.bluetooth.spezi.core.model.BluetoothState
import edu.stanford.bdh.engagehf.bluetooth.spezi.model.BluetoothDevice
import edu.stanford.bdh.engagehf.bluetooth.spezi.utils.BTUUID
import kotlinx.coroutines.flow.Flow
import kotlin.reflect.KClass
import kotlin.time.Duration

class Bluetooth(
    @ApplicationContext val context: Context,
    val configuration: Set<DeviceDiscoveryDescriptor>
) {

    private class Storage {
        val nearbyDevices = mutableListOf<BluetoothDevice>()
    }

    private val manager = BluetoothManager(context)
    private val storage = Storage()

    val state: BluetoothState
        get() = manager.state

    val isScanning: Boolean
        get() = manager.isScanning

    val stateSubscription: Flow<BluetoothState>
        get() = manager.stateSubscription

    val hasConnectedDevices: Boolean
        get() = manager.hasConnectedDevices

    fun powerOn() {
        manager.powerOn()
    }

    fun powerOff() {
        manager.powerOff()
    }

    fun <Device: BluetoothDevice> nearbyDevices(type: KClass<Device>): List<Device> {
        return storage.nearbyDevices.mapNotNull { it as? Device }   // TODO
    }

    suspend fun <Device: BluetoothDevice> retrieveDevice(
        uuid: BTUUID,
        type: KClass<Device>
    ): Device? {
        TODO()
    }

    fun scanNearbyDevices(
        minimumRssi: Int? = null,
        advertisementStaleInterval: Duration? = null,
        autoConnect: Boolean = false
    ) {
        TODO()
        /*
        manager.scanNearbyDevices(
            discovery = configuration,
            minimumRssi = minimumRssi, //
            advertisementStaleInterval = advertisementStaleInterval,
            autoConnect = autoConnect
        )
         */
    }

    fun stopScanning() {
        manager.stopScanning()
    }
}