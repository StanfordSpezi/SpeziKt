package edu.stanford.spezi.modules.bluetooth.core.configuration

import edu.stanford.spezi.modules.bluetooth.BTUUID

class DeviceDescription(
    val services: Set<ServiceDescription>?,
) {
    private val _services by lazy { services?.associateBy { it.serviceId } }

    fun description(serviceId: BTUUID) = _services?.get(serviceId)
}
