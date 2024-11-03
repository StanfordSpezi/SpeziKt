package edu.stanford.spezi.modules.bluetooth

import android.os.ParcelUuid
import java.util.UUID

data class BTUUID(
    internal val parcelUuid: ParcelUuid
) {
    constructor(uuid: String) : this(parcelUuid = ParcelUuid.fromString(uuid))

    constructor(uuid: UUID) : this(uuid.toString())

    override fun toString(): String {
        return parcelUuid.toString()
    }
}
