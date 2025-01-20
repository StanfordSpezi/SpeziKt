package edu.stanford.spezi.modules.bluetooth.core.model

import edu.stanford.spezi.modules.bluetooth.BTUUID

// TODO which one can be used. Use constructor parameters to build error messages / failures
sealed class BluetoothError(
    message: String? = null,
    cause: Throwable? = null,
) : Throwable(message, cause) {

    class IncompatibleDataFormat : BluetoothError()

    class NotPresent(
        service: BTUUID?,
        characteristic: BTUUID,
    ) : BluetoothError()

    class ControlPointRequiresNotifying(
        service: BTUUID,
        characteristic: BTUUID,
    ) : BluetoothError()

    class ControlPointInProgress(
        service: BTUUID,
        characteristic: BTUUID,
    ) : BluetoothError()
}
