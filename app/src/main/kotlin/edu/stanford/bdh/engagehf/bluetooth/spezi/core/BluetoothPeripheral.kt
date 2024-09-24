package edu.stanford.bdh.engagehf.bluetooth.spezi.core

import android.os.ParcelUuid
import edu.stanford.bdh.engagehf.bluetooth.spezi.utils.BTUUID
import edu.stanford.bdh.engagehf.bluetooth.spezi.core.model.GATTCharacteristic
import edu.stanford.bdh.engagehf.bluetooth.spezi.core.model.GATTService
import edu.stanford.bdh.engagehf.bluetooth.spezi.core.model.AdvertisementData
import edu.stanford.bdh.engagehf.bluetooth.spezi.core.model.OnChangeRegistration
import edu.stanford.bdh.engagehf.bluetooth.spezi.core.model.PeripheralState
import java.util.Date

class BluetoothPeripheral(
    val id: ParcelUuid
) {

    val name: String? get() = TODO()
    val rssi: Int get() = TODO()
    val state: PeripheralState get() = TODO()
    val advertisementData: AdvertisementData get() = TODO()
    val services: List<GATTService>? get() = TODO()
    val lastActivity: Date get() = TODO()
    val nearby: Boolean get() = TODO()

    suspend fun connect(): Unit = TODO()
    fun disconnect(): Unit = TODO()

    fun getService(id: BTUUID): GATTService? = TODO()
    fun getCharacteristic(characteristicId: BTUUID, serviceId: ParcelUuid): GATTCharacteristic? = TODO()

    fun registerOnChangeHandler(
        characteristic: GATTCharacteristic,
        onChange: (ByteArray) -> Unit
    ): OnChangeRegistration = TODO()

    fun registerOnChangeHandler(
        service: ParcelUuid,
        characteristic: ParcelUuid,
        onChange: (ByteArray) -> Unit
    ): OnChangeRegistration = TODO()

    fun enableNotifications(
        enabled: Boolean = true,
        serviceId: ParcelUuid,
        characteristicId: ParcelUuid
    ): Unit = TODO()

    suspend fun setNotifications(enabled: Boolean, characteristic: GATTCharacteristic): Unit = TODO()

    suspend fun write(data: ByteArray, characteristic: GATTCharacteristic): Unit = TODO()

    suspend fun writeWithoutResponse(data: ByteArray, characteristic: GATTCharacteristic): Unit = TODO()

    suspend fun read(characteristic: GATTCharacteristic): ByteArray = TODO()

    suspend fun readRSSI(): Int = TODO()
}
