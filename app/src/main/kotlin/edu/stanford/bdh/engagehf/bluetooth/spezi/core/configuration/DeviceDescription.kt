package edu.stanford.bdh.engagehf.bluetooth.spezi.core.configuration

import edu.stanford.bdh.engagehf.bluetooth.spezi.utils.BTUUID

data class DeviceDescription(
    val services: Set<ServiceDescription>? = null,
) {
    fun description(identifier: BTUUID): ServiceDescription? =
        services?.firstOrNull { it.identifier == identifier }
}
