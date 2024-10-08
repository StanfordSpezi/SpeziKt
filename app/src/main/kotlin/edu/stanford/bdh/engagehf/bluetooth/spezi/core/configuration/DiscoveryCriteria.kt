package edu.stanford.bdh.engagehf.bluetooth.spezi.core.configuration

import edu.stanford.bdh.engagehf.bluetooth.spezi.utils.BTUUID
import edu.stanford.bdh.engagehf.bluetooth.spezi.core.model.ManufacturerIdentifier

sealed class DiscoveryCriteria {
    data class AdvertisedServices(
        val uuids: List<BTUUID>
    ): DiscoveryCriteria()

    data class Accessory(
        val manufacturer: ManufacturerIdentifier,
        val uuids: List<BTUUID>
    ): DiscoveryCriteria()

    val discoveryIds: List<BTUUID> get() =
        when (this) {
            is AdvertisedServices -> this.uuids
            is Accessory -> this.uuids
        }
}