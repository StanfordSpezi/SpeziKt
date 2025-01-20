package edu.stanford.spezi.modules.bluetooth.core.configuration

import edu.stanford.spezi.modules.bluetooth.BTUUID

data class CharacteristicDescription(
    val characteristicId: BTUUID,
    val discoverDescriptors: Boolean,
    val autoRead: Boolean,
)
