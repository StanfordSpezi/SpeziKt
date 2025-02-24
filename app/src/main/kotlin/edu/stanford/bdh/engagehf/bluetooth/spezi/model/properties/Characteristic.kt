package edu.stanford.bdh.engagehf.bluetooth.spezi.model.properties

import edu.stanford.bdh.engagehf.bluetooth.spezi.model.BluetoothService
import edu.stanford.bdh.engagehf.bluetooth.spezi.model.propertySupport.CharacteristicAccessor
import edu.stanford.bdh.engagehf.bluetooth.spezi.utils.BTUUID
import kotlin.reflect.KProperty

// TODO: We might need to build this different, except for the public interface
class Characteristic<Value>(
    val identifier: BTUUID,
    val notify: Boolean = false,
    val autoRead: Boolean = true,
) {
    operator fun <Service : BluetoothService> getValue(
        thisRef: Service,
        property: KProperty<*>,
    ): Value? = TODO()

    val accessor: CharacteristicAccessor<Value> get() = TODO()

    companion object {
        operator fun <Value> invoke(
            identifier: String,
            notify: Boolean = false,
            autoRead: Boolean = true,
        ): Characteristic<Value> {
            return Characteristic(BTUUID(identifier), notify, autoRead)
        }
    }
}
