package edu.stanford.spezikt.core.bluetooth.data.model

import edu.stanford.spezi.utils.UUID
import java.util.UUID

internal enum class BLEServiceType(val service: UUID, val characteristic: UUID) {
    WEIGHT(
        service = UUID("0000181d-0000-1000-8000-00805f9b34fb"),
        characteristic = UUID("00002a9d-0000-1000-8000-00805f9b34fb")
    ),
    BLOOD_PRESSURE(
        service = UUID("00001810-0000-1000-8000-00805f9b34fb"),
        characteristic = UUID("00002a35-0000-1000-8000-00805f9b34fb")
    )
}