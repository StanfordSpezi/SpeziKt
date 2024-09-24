package edu.stanford.bdh.engagehf.bluetooth.spezi.configuration

import edu.stanford.bdh.engagehf.bluetooth.spezi.model.BluetoothDevice
import edu.stanford.bdh.engagehf.bluetooth.spezi.core.configuration.DiscoveryCriteria
import kotlin.reflect.KClass

data class Discover<Device: BluetoothDevice>(
    val discoveryCriteria: DiscoveryCriteria,
    val deviceType: KClass<Device>,
)
