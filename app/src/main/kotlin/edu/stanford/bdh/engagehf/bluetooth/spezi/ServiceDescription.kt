package edu.stanford.bdh.engagehf.bluetooth.spezi

import java.util.UUID

data class ServiceDescription(
    val identifier: UUID,
    val characteristics: Set<CharacteristicDescription>? = null
) {
    fun description(identifier: UUID): CharacteristicDescription? =
        characteristics?.firstOrNull { it.identifier == identifier }
}