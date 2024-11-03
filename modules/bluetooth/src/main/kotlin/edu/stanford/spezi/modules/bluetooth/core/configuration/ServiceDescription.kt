package edu.stanford.spezi.modules.bluetooth.core.configuration

import edu.stanford.spezi.modules.bluetooth.BTUUID

class ServiceDescription(
    val serviceId: BTUUID,
    val characteristics: Set<CharacteristicDescription>?,
) {
    private val _characteristics by lazy { characteristics?.associateBy { it.characteristicId } }

    fun description(characteristicsId: BTUUID): CharacteristicDescription? =
        _characteristics?.get(characteristicsId)
}
