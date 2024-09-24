package edu.stanford.bdh.engagehf.bluetooth.spezi.core.configuration

import android.os.ParcelUuid

data class DeviceDescription(
    val services: Set<ServiceDescription>? = null
) {
    fun description(identifier: ParcelUuid): ServiceDescription? =
        services?.firstOrNull { it.identifier == identifier }
}
