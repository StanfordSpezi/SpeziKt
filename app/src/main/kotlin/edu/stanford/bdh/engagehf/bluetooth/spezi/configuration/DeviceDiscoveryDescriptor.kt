package edu.stanford.bdh.engagehf.bluetooth.spezi.configuration

import edu.stanford.bdh.engagehf.bluetooth.spezi.core.configuration.DiscoveryCriteria
import edu.stanford.bdh.engagehf.bluetooth.spezi.model.BluetoothDevice
import kotlin.reflect.KClass

data class DeviceDiscoveryDescriptor(
    val discoveryCriteria: DiscoveryCriteria,
    val deviceType: KClass<BluetoothDevice>,
)
