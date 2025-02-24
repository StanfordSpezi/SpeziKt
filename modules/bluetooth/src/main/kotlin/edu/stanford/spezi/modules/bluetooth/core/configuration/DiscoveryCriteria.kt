package edu.stanford.spezi.modules.bluetooth.core.configuration

import edu.stanford.spezi.modules.bluetooth.BTUUID
import edu.stanford.spezi.modules.bluetooth.BluetoothService
import edu.stanford.spezi.modules.bluetooth.core.model.ManufacturerIdentifier

sealed class DiscoveryCriteria(internal open val services: List<BTUUID>) {
    internal class AdvertisedServices(
        override val services: List<BTUUID>,
    ) : DiscoveryCriteria(services = services)

    internal class Accessory(
        val manufacturer: ManufacturerIdentifier,
        override val services: List<BTUUID>,
    ) : DiscoveryCriteria(services = services)

    companion object
}

private typealias Extension = DiscoveryCriteria.Companion

fun Extension.advertisedService(uuid: BTUUID): DiscoveryCriteria =
    DiscoveryCriteria.AdvertisedServices(services = listOf(uuid))

fun Extension.advertisedServices(vararg services: BTUUID): DiscoveryCriteria =
    DiscoveryCriteria.AdvertisedServices(services = services.toList())

fun <S : BluetoothService> Extension.advertisedService(service: S): DiscoveryCriteria =
    advertisedService(uuid = service.id)
