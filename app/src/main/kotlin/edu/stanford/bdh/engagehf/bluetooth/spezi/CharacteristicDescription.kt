package edu.stanford.bdh.engagehf.bluetooth.spezi

import java.util.UUID

data class CharacteristicDescription(
    val identifier: UUID,
    val discoverDescriptors: Boolean = false,
    val autoRead: Boolean = true // TODO: Think about renaming
)