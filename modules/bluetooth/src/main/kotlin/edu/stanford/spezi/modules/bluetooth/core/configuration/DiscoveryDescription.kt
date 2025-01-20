package edu.stanford.spezi.modules.bluetooth.core.configuration

data class DiscoveryDescription(
    val device: DeviceDescription,
    val criteria: DiscoveryCriteria,
)
