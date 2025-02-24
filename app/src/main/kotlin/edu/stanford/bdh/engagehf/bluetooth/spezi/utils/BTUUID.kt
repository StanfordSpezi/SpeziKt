package edu.stanford.bdh.engagehf.bluetooth.spezi.utils

import android.os.ParcelUuid

data class BTUUID(val parcelUuid: ParcelUuid) {
    companion object {
        private const val SHORT_UUID_LENGTH = 4

        operator fun invoke(string: String): BTUUID {
            val uuid = if (string.length == SHORT_UUID_LENGTH) {
                "0000$string-0000-1000-8000-00805F9B34FB"
            } else {
                string
            }
            return BTUUID(ParcelUuid.fromString(uuid))
        }
    }
}
