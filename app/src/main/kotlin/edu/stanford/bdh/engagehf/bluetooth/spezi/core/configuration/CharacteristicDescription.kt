package edu.stanford.bdh.engagehf.bluetooth.spezi.core.configuration

import edu.stanford.bdh.engagehf.bluetooth.spezi.utils.BTUUID

data class CharacteristicDescription(
    val identifier: BTUUID,
    val discoverDescriptors: Boolean = false,
    val autoRead: Boolean = true, // TODO: Think about renaming
)
