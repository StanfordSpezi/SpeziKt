package edu.stanford.bdh.engagehf.bluetooth.spezi

import androidx.compose.runtime.CompositionLocal
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf
import java.util.UUID
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.KProperty1

// TODO: We might need to build this different, except for the public interface
class Characteristic<Value> {

    data class Storage(
        val identifier: UUID,
        val defaultNotify: DefaultNotifyState,
        val autoRead: Boolean,
    ) {
        enum class DefaultNotifyState(val rawValue: UByte) {
            DISABLED(0u),
            ENABLED(1u),
            COLLECTED_DISABLED(2u),
            COLLECTED_ENABLED(3u);

            val defaultNotify: Boolean get () =
                when (this) {
                    DISABLED, COLLECTED_DISABLED -> false
                    ENABLED, COLLECTED_ENABLED -> true
                }

            val completed: Boolean get() =
                when (this) {
                    DISABLED, ENABLED -> false
                    COLLECTED_DISABLED, COLLECTED_ENABLED -> true
                }

            companion object {
                fun invoke(defaultNotify: Boolean): DefaultNotifyState =
                    if (defaultNotify) ENABLED else DISABLED

                fun collected(notify: Boolean): DefaultNotifyState =
                    if (notify) COLLECTED_ENABLED else COLLECTED_DISABLED
            }
        }
    }

    operator fun getValue(thisRef: Any?, property: KProperty<*>): Value {

    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: Value) {

    }

    val accessor: CharacteristicAccessor<Value> get() {
        return CharacteristicAccessor(this)
    }
}

class CharacteristicAccessor<Value> {

}

class MockDevice: BluetoothDevice {
    val id by DeviceState(BluetoothPeripheral::id)
    val name by DeviceState(BluetoothPeripheral::name)
    val state by DeviceState(BluetoothPeripheral::state)
    val rssi by DeviceState(BluetoothPeripheral::rssi)
}

class BluetoothPeripheral(
    val id: UUID,
    val name: String,
    val state: PeripheralState,
    val rssi: Double
) {

}

class Bluetooth {

}

data class DeviceState<Value>(val property: KProperty1<BluetoothPeripheral, Value>) {
    private var peripheral: BluetoothPeripheral? = null

    fun inject(bluetooth: Bluetooth, peripheral: BluetoothPeripheral) {
        this.peripheral = peripheral
    }

    operator fun getValue(thisRef: BluetoothDevice, property: KProperty<*>): Value {
        return this.peripheral?.let {
            this.property.get(it)
        } ?: throw Error() // TODO: Handle error better
    }
}

data class ConnectedDevices<Device: BluetoothDevice>(val devices: List<Device> = emptyList())

private var localConnectedDevices = mutableMapOf<KClass<*>, Any>()

fun <Device: BluetoothDevice> localConnectedDevices(type: KClass<Device>): ProvidableCompositionLocal<ConnectedDevices<Device>> {
    @Suppress("UNCHECKED_CAST") // TODO: Think about whether we can get rid of this.
    val existingValue = localConnectedDevices[type]?.let { return@let it as? ProvidableCompositionLocal<ConnectedDevices<Device>> }
    if (existingValue != null) return existingValue
    val newValue = compositionLocalOf { ConnectedDevices<Device>() }
    localConnectedDevices[type] = newValue
    return newValue
}

