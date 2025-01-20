package edu.stanford.bdh.engagehf.bluetooth.spezi.core.model

import android.os.ParcelUuid

sealed class BluetoothError : Error() {
    @Suppress("UnusedPrivateMember")
    data object IncompatibleDataFormat : BluetoothError() {
        private fun readResolve(): Any = IncompatibleDataFormat // TODO: What is this?
    }

    data class NotPresent(
        val service: ParcelUuid?,
        val characteristic: ParcelUuid,
    ) : BluetoothError()

    data class ControlPointRequiresNotifying(
        val service: ParcelUuid,
        val characteristic: ParcelUuid,
    ) : BluetoothError()

    data class ControlPointInProgress(
        val service: ParcelUuid,
        val characteristic: ParcelUuid,
    ) : BluetoothError()
}
