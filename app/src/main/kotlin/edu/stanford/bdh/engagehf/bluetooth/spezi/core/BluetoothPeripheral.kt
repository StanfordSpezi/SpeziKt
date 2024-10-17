package edu.stanford.bdh.engagehf.bluetooth.spezi.core

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanResult
import android.content.Context
import android.os.ParcelUuid
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import edu.stanford.bdh.engagehf.bluetooth.spezi.utils.BTUUID
import edu.stanford.bdh.engagehf.bluetooth.spezi.core.model.GATTCharacteristic
import edu.stanford.bdh.engagehf.bluetooth.spezi.core.model.GATTService
import edu.stanford.bdh.engagehf.bluetooth.spezi.core.model.AdvertisementData
import edu.stanford.bdh.engagehf.bluetooth.spezi.core.model.OnChangeRegistration
import edu.stanford.bdh.engagehf.bluetooth.spezi.core.model.PeripheralState
import java.util.Date

class BluetoothPeripheral(
    private val manager: BluetoothManager,
    private val result: ScanResult,
) {
    class Storage {

    }

    private var gatt: BluetoothGatt? = null
    private val storage = Storage()

    val id: BTUUID @SuppressLint("MissingPermission")
        get() = BTUUID(result.device.uuids.first())

    val name: String? @SuppressLint("MissingPermission")
        get() = result.device.name

    val rssi: Int get() = result.rssi
    val state: PeripheralState get() = TODO()
    val advertisementData: AdvertisementData
        get() = AdvertisementData(
            localName = result.scanRecord?.deviceName,
            manufacturerData = null, // TODO: result.scanRecord?.manufacturerSpecificData,
            serviceData = result.scanRecord?.serviceData?.mapKeys { BTUUID(it.key) },
            serviceIdentifiers = result.scanRecord?.serviceUuids?.map { BTUUID(it) },
            overflowServiceIdentifiers = null, // TODO: Figure out whether this exists or how it is set on iOS
            txPowerLevel = result.scanRecord?.txPowerLevel,
            isConnectable = result.isConnectable,
            solicitedServiceIdentifiers = result.scanRecord?.serviceSolicitationUuids?.map { BTUUID(it) }
        )

    val services: List<GATTService>? get() = TODO()
    val lastActivity: Date get() = TODO()
    val nearby: Boolean get() = TODO()

    @SuppressLint("MissingPermission")
    suspend fun connect(context: Context) {
        gatt = result.device.connectGatt(context, true, null)
    }
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
