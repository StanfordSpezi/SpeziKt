package edu.stanford.bdh.engagehf.bluetooth.spezi.core.configuration

import android.os.ParcelUuid
import edu.stanford.bdh.engagehf.bluetooth.spezi.utils.BTUUID

data class ServiceDescription(
    val identifier: BTUUID,
    val characteristics: Set<CharacteristicDescription>? = null
) {
    fun description(identifier: BTUUID): CharacteristicDescription? =
        characteristics?.firstOrNull { it.identifier == identifier }
}