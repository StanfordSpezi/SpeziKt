package edu.stanford.spezi.modules.bluetooth.core.model

import edu.stanford.spezi.modules.bluetooth.BTUUID

data class AdvertisementData(
    val localName: String?,
    val manufacturerData: ByteArray?,
    val serviceData: Map<BTUUID, ByteArray>?,
    val serviceIdentifiers: List<BTUUID>?,
    val overflowServiceIdentifiers: List<BTUUID>?,
    val txPowerLevel: Int?,
    val isConnectable: Boolean?,
    val solicitedServiceIdentifiers: List<BTUUID>?,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is AdvertisementData) return false

        return localName == other.localName &&
            manufacturerData.contentEquals(other.manufacturerData) &&
            serviceData == other.serviceData &&
            serviceIdentifiers == other.serviceIdentifiers &&
            overflowServiceIdentifiers == other.overflowServiceIdentifiers &&
            txPowerLevel == other.txPowerLevel &&
            isConnectable == other.isConnectable &&
            solicitedServiceIdentifiers == other.solicitedServiceIdentifiers
    }


    override fun hashCode(): Int {
        var result = localName?.hashCode() ?: 0
        result = 31 * result + (manufacturerData?.contentHashCode() ?: 0)
        result = 31 * result + (serviceData?.hashCode() ?: 0)
        result = 31 * result + (serviceIdentifiers?.hashCode() ?: 0)
        result = 31 * result + (overflowServiceIdentifiers?.hashCode() ?: 0)
        result = 31 * result + (txPowerLevel ?: 0)
        result = 31 * result + (isConnectable?.hashCode() ?: 0)
        result = 31 * result + (solicitedServiceIdentifiers?.hashCode() ?: 0)
        return result
    }
}
