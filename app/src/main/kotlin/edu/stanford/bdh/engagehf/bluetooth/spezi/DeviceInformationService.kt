package edu.stanford.bdh.engagehf.bluetooth.spezi

import edu.stanford.bdh.engagehf.bluetooth.spezi.model.BluetoothService
import edu.stanford.bdh.engagehf.bluetooth.spezi.model.properties.Characteristic
import edu.stanford.bdh.engagehf.bluetooth.spezi.utils.BTUUID

data class DeviceInformationService(override val id: BTUUID = BTUUID("180A")) : BluetoothService {
    val manufacturerName by Characteristic<String>("2A29")
    val modelNumber by Characteristic<String>("2A24")
    val serialNumber by Characteristic<String>("2A25")
    val hardwareRevision by Characteristic<String>("2A27")
    val firmwareRevision by Characteristic<String>("2A26")
    val softwareRevision by Characteristic<String>("2A28")

    val systemId by Characteristic<ULong>("2A23")
    val regulatoryCertificationDataList by Characteristic<ByteArray>("2A2A")
    val pnpId by Characteristic<PnPID>("2A50")
}

data class PnPID(val string: String) // TODO: Figure out what this is
