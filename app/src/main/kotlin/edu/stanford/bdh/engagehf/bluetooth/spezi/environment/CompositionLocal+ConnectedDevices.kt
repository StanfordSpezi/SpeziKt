package edu.stanford.bdh.engagehf.bluetooth.spezi.environment

import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf
import edu.stanford.bdh.engagehf.bluetooth.spezi.model.BluetoothDevice
import edu.stanford.bdh.engagehf.bluetooth.spezi.utils.ConnectedDevices
import kotlin.reflect.KClass

private var localConnectedDevices = mutableMapOf<KClass<*>, Any>()

fun <Device: BluetoothDevice> localConnectedDevices(type: KClass<Device>): ProvidableCompositionLocal<ConnectedDevices<Device>> {
    @Suppress("UNCHECKED_CAST") // TODO: Think about whether we can get rid of this.
    val existingValue = localConnectedDevices[type]?.let { return@let it as? ProvidableCompositionLocal<ConnectedDevices<Device>> }
    if (existingValue != null) return existingValue
    val newValue = compositionLocalOf { ConnectedDevices<Device>() }
    localConnectedDevices[type] = newValue
    return newValue
}