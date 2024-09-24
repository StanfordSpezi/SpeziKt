package edu.stanford.bdh.engagehf.bluetooth.spezi.core.model

enum class BluetoothState(private val rawValue: UByte) {
    UNKNOWN(0u),
    POWERED_OFF(1u),
    UNSUPPORTED(2u),
    UNAUTHORIZED(3u),
    POWERED_ON(4u)
}
