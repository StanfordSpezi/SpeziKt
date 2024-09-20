package edu.stanford.bdh.engagehf.bluetooth.spezi

import java.util.UUID

sealed class BluetoothError: Error() {
    data object IncompatibleDataFormat: BluetoothError() {
        private fun readResolve(): Any = IncompatibleDataFormat // TODO: What is this?
    }

    data class NotPresent(
        val service: UUID?,
        val characteristic: UUID
    ): BluetoothError()

    data class ControlPointRequiresNotifying(
        val service: UUID,
        val characteristic: UUID
    ): BluetoothError()

    data class ControlPointInProgress(
        val service: UUID,
        val characteristic: UUID
    ): BluetoothError()
}