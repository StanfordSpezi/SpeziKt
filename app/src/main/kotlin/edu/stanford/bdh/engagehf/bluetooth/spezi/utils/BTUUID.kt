package edu.stanford.bdh.engagehf.bluetooth.spezi.utils

import android.os.ParcelUuid

data class BTUUID(val parcelUuid: ParcelUuid) {
    companion object {
        operator fun invoke(string: String): BTUUID {
            return when (string.length) {
                4 -> BTUUID(ParcelUuid.fromString("0000$string-0000-1000-8000-00805F9B34FB"))
                else -> BTUUID(ParcelUuid.fromString(string))
            }
        }
    }
}