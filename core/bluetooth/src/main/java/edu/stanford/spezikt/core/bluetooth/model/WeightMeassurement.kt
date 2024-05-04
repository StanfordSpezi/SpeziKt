package edu.stanford.spezikt.core.bluetooth.model

import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import kotlin.experimental.and

data class WeightMeasurement(
    val weight: Double?,
    val zonedDateTime: ZonedDateTime?,
    val userId: Int?,
    val bmi: Double?,
    val height: Double?
) : Measurement() {
    companion object {
        fun fromByteArray(bytes: ByteArray): WeightMeasurement {
            return interpretWeightMeasurement(bytes)
        }
    }
}


private fun interpretWeightMeasurement(bytes: ByteArray): WeightMeasurement {
    val flags = bytes[0]
    val unitKg = (flags and 0b00000001).toInt() == 0  // Check if bit 0 is 0 for kg, 1 for lb

    val weight = if (unitKg) {
        // Kilograms, resolution of 0.005 kg
        (bytes[1].toInt() and 0xFF or (bytes[2].toInt() and 0xFF shl 8)) * 0.005
    } else {
        // Pounds, resolution of 0.01 lb
        (bytes[1].toInt() and 0xFF or (bytes[2].toInt() and 0xFF shl 8)) * 0.01
    }

    val zonedDateTime = if (flags and 0b00000010 > 0) {

        val year = bytes[3].toInt() and 0xFF or (bytes[4].toInt() and 0xFF shl 8)
        val month = bytes[5].toInt()
        val day = bytes[6].toInt()
        val hour = bytes[7].toInt()
        val minute = bytes[8].toInt()
        val second = bytes[9].toInt()
        if (year == 0 || month == 0 || day == 0) null
        else {
            val localDateTime = LocalDateTime.of(year, month, day, hour, minute, second)
            val zoneId = ZoneId.systemDefault()
            ZonedDateTime.of(localDateTime, zoneId)
        }
    } else null

    val userId = if (flags and 0b00000100 > 0) bytes[10].toInt() else null
    val bmi =
        if (flags and 0b00001000 > 0) (bytes[11].toInt() and 0xFF or (bytes[12].toInt() and 0xFF shl 8)) * 0.1 else null
    val height =
        if (flags and 0b00001000 > 0) (bytes[13].toInt() and 0xFF or (bytes[14].toInt() and 0xFF shl 8)) * (if (unitKg) 0.005 else 0.1) else null

    return WeightMeasurement(
        weight = weight,
        zonedDateTime = zonedDateTime,
        userId = userId,
        bmi = bmi,
        height = height
    )
}
