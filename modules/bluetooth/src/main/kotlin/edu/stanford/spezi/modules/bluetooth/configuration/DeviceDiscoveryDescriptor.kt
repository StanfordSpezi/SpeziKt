package edu.stanford.spezi.modules.bluetooth.configuration

import edu.stanford.spezi.modules.bluetooth.core.configuration.DiscoveryCriteria
import edu.stanford.spezi.modules.bluetooth.SpeziBluetoothDevice
import kotlin.reflect.KClass

// TODO, merging Discover class in iOS into this as they are literally the same
// TODO, review whether we can start start something with KClass since we can't restrict static requirements in kotlin
// Alternative: we can enforce a bluetooth device instance, which we manipulate internally to set the values
data class DeviceDiscoveryDescriptor<S : SpeziBluetoothDevice>(
    val discoveryCriteria: DiscoveryCriteria,
    val deviceType: KClass<S>,
) {
    companion object {
        inline operator fun <reified S : SpeziBluetoothDevice> invoke(
            discoveryCriteria: DiscoveryCriteria,
        ): DeviceDiscoveryDescriptor<S> = DeviceDiscoveryDescriptor(
            discoveryCriteria,
            deviceType = S::class
        )
    }
}
