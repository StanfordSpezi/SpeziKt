package edu.stanford.bdh.engagehf.bluetooth.spezi

import java.util.UUID

data class AdvertisementData(
    val localName: String?,
    val manufacturerData: ByteArray?,
    val serviceData: Map<UUID, ByteArray>?,
    val serviceIdentifiers: List<UUID>?,
    val overflowServiceIdentifiers: List<UUID>?,
    val txPowerLevel: Double?,
    val isConnectable: Boolean?,
    val solicitedServiceIdentifiers: List<UUID>?
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AdvertisementData

        if (localName != other.localName) return false
        if (manufacturerData != null) {
            if (other.manufacturerData == null) return false
            if (!manufacturerData.contentEquals(other.manufacturerData)) return false
        } else if (other.manufacturerData != null) return false
        if (serviceData != other.serviceData) return false
        if (serviceIdentifiers != other.serviceIdentifiers) return false
        if (overflowServiceIdentifiers != other.overflowServiceIdentifiers) return false
        if (txPowerLevel != other.txPowerLevel) return false
        if (isConnectable != other.isConnectable) return false
        if (solicitedServiceIdentifiers != other.solicitedServiceIdentifiers) return false

        return true
    }

    override fun hashCode(): Int {
        var result = localName?.hashCode() ?: 0
        result = 31 * result + (manufacturerData?.contentHashCode() ?: 0)
        result = 31 * result + (serviceData?.hashCode() ?: 0)
        result = 31 * result + (serviceIdentifiers?.hashCode() ?: 0)
        result = 31 * result + (overflowServiceIdentifiers?.hashCode() ?: 0)
        result = 31 * result + (txPowerLevel?.hashCode() ?: 0)
        result = 31 * result + (isConnectable?.hashCode() ?: 0)
        result = 31 * result + (solicitedServiceIdentifiers?.hashCode() ?: 0)
        return result
    }
}