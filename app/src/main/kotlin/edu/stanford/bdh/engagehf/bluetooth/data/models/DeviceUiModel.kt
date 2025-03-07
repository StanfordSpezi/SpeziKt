package edu.stanford.bdh.engagehf.bluetooth.data.models

import edu.stanford.spezi.spezi.ui.resources.StringResource

data class DeviceUiModel(
    val name: String,
    val summary: String,
    val connected: Boolean,
    val lastSeen: StringResource,
)
