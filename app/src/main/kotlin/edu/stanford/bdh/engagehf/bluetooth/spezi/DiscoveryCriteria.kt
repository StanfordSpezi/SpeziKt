package edu.stanford.bdh.engagehf.bluetooth.spezi

import java.util.UUID

sealed class DiscoveryCriteria {
    data class AdvertisedServices(
        val uuids: List<UUID>
    ): DiscoveryCriteria()

    data class Accessory(
        val manufacturer: ManufacturerIdentifier,
        val uuids: List<UUID>
    ): DiscoveryCriteria()

    val discoveryIds: List<UUID> get() =
        when (this) {
            is AdvertisedServices -> this.uuids
            is Accessory -> this.uuids
        }


}