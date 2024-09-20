package edu.stanford.bdh.engagehf.bluetooth.spezi

import java.util.UUID

data class DeviceDescription(
    val services: Set<ServiceDescription>? = null
) {
    fun description(identifier: UUID): ServiceDescription? =
        services?.firstOrNull { it.identifier == identifier }
}
