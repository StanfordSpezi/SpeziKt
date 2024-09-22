package edu.stanford.spezi.core.bluetooth.data.model

import kotlinx.serialization.Serializable

@Serializable
data class BLEDevice(
    val address: String,
    val name: String,
    val connected: Boolean,
)
