@file:Suppress("MagicNumber")
package edu.stanford.bdh.engagehf.bluetooth.service.mapper

import android.bluetooth.BluetoothGattCharacteristic
import edu.stanford.bdh.engagehf.bluetooth.service.BLEServiceType
import edu.stanford.bdh.engagehf.bluetooth.service.Measurement
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import javax.inject.Inject
import kotlin.experimental.and

/**
 * Implementation of a child measurement mapper for weight measurements.
 */
internal class WeightMeasurementMapper @Inject constructor() : MeasurementMapper.Child {

    /**
     * Checks if the given Bluetooth GATT characteristic is recognized as a weight measurement.
     *
     * @param characteristic The Bluetooth GATT characteristic to be recognized.
     * @return true if the characteristic is recognized as a weight measurement, false otherwise.
     */
    override fun recognises(characteristic: BluetoothGattCharacteristic?): Boolean {
        return with(BLEServiceType.WEIGHT) { characteristic?.let { service == it.service.uuid && this.characteristic == it.uuid } ?: false }
    }

    /**
     * Maps a Bluetooth GATT characteristic and its data to a weight measurement.
     *
     * @param characteristic The Bluetooth GATT characteristic to be mapped.
     * @param data The byte array representing the data of the characteristic.
     * @return The weight measurement, or null if the characteristic is not recognized or mapping fails.
     */
    override suspend fun map(characteristic: BluetoothGattCharacteristic?, data: ByteArray): Measurement? {
        return if (recognises(characteristic).not()) null else runCatching { interpretWeightMeasurement(data) }.getOrNull()
    }

    private fun interpretWeightMeasurement(data: ByteArray): Measurement.Weight {
        val flags = data[0]
        val unitKg = (flags and 0b00000001).toInt() == 0 // Check if bit 0 is 0 for kg, 1 for lb

        val weight = if (unitKg) {
            // Kilograms, resolution of 0.005 kg
            (data[1].toInt() and 0xFF or (data[2].toInt() and 0xFF shl 8)) * 0.005
        } else {
            // Pounds, resolution of 0.01 lb
            (data[1].toInt() and 0xFF or (data[2].toInt() and 0xFF shl 8)) * 0.01
        }

        val zonedDateTime = if (flags and 0b00000010 > 0) {
            val year = data[3].toInt() and 0xFF or (data[4].toInt() and 0xFF shl 8)
            val month = data[5].toInt()
            val day = data[6].toInt()
            val hour = data[7].toInt()
            val minute = data[8].toInt()
            val second = data[9].toInt()
            if (year == 0 || month == 0 || day == 0) {
                null
            } else {
                val localDateTime = LocalDateTime.of(year, month, day, hour, minute, second)
                val zoneId = ZoneId.systemDefault()
                ZonedDateTime.of(localDateTime, zoneId)
            }
        } else {
            null
        }

        val userId = if (flags and 0b00000100 > 0) data[10].toInt() else null
        val bmi = if (flags and 0b00001000 > 0) (data[11].toInt() and 0xFF or (data[12].toInt() and 0xFF shl 8)) * 0.1 else null
        val height = if (flags and 0b00001000 > 0) {
            (data[13].toInt() and 0xFF or (data[14].toInt() and 0xFF shl 8)) * (if (unitKg) 0.005 else 0.1)
        } else {
            null
        }

        return Measurement.Weight(
            weight = weight,
            zonedDateTime = zonedDateTime,
            userId = userId,
            bmi = bmi,
            height = height
        )
    }
}
