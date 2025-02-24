package edu.stanford.bdh.engagehf.bluetooth.spezi.core.model

// TODO: We use Device pretty consistently, why now Peripheral?
enum class PeripheralState(private val value: UByte) {
    DISCONNECTED(0u),
    CONNECTING(1u),
    CONNECTED(2u),
    DISCONNECTING(3u),
}
