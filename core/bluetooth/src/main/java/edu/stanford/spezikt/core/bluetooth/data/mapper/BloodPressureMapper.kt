package edu.stanford.spezikt.core.bluetooth.data.mapper

import android.bluetooth.BluetoothGattCharacteristic
import edu.stanford.spezikt.core.bluetooth.data.model.BLEServiceType
import edu.stanford.spezikt.core.bluetooth.data.model.Measurement
import javax.inject.Inject

internal class BloodPressureMapper @Inject constructor() : MeasurementMapper.Child {

    override fun recognises(characteristic: BluetoothGattCharacteristic?): Boolean {
        return with(BLEServiceType.BLOOD_PRESSURE) {
            characteristic?.let { service == it.service.uuid && this.characteristic == it.uuid } ?: false
        }
    }

    override suspend fun map(characteristic: BluetoothGattCharacteristic?, data: ByteArray): Measurement? {
        return if (recognises(characteristic).not()) null else runCatching { interpretBloodPressureMeasurement(data = data) }.getOrNull()
    }

    private fun interpretBloodPressureMeasurement(data: ByteArray): Measurement.BloodPressure {
        val flags = Measurement.BloodPressure.Flags(
            bloodPressureUnitsFlag = (data[0].toInt() and 0b00000001) != 0,
            timeStampFlag = (data[0].toInt() and 0b00000010) != 0,
            pulseRateFlag = (data[0].toInt() and 0b00000100) != 0,
            userIdFlag = (data[0].toInt() and 0b00001000) != 0,
            measurementStatusFlag = (data[0].toInt() and 0b00100000) != 0
        )

        val systolic = (data[1].toInt() and 0xFF).toFloat()
        val diastolic = (data[3].toInt() and 0xFF).toFloat()
        val meanArterialPressure = (data[5].toInt() and 0xFF).toFloat()
        val timestampYear = (data[7].toInt() and 0xFF)
        val timestampMonth = (data[9].toInt() and 0xFF)
        val timestampDay = (data[10].toInt() and 0xFF)
        val timeStampHour = (data[11].toInt() and 0xFF)
        val timeStampMinute = (data[12].toInt() and 0xFF)
        val timeStampSecond = (data[13].toInt() and 0xFF)
        val pulseRate = (data[14].toInt() and 0xFF).toFloat()
        val userId = (data[16].toInt() and 0xFF)

        val measurementStatus = Measurement.BloodPressure.Status(
            bodyMovementDetectionFlag = (data[17].toInt() and 0b0000000000000001) != 0,
            cuffFitDetectionFlag = (data[17].toInt() and 0b0000000000000010) != 0,
            irregularPulseDetectionFlag = (data[17].toInt() and 0b0000000000000100) != 0,
            pulseRateRangeDetectionFlags = (data[17].toInt() shr 3) and 0b11,
            measurementPositionDetectionFlag = (data[17].toInt() and 0b00100000) != 0
        )

        return Measurement.BloodPressure(
            flags = flags,
            systolic = systolic,
            diastolic = diastolic,
            meanArterialPressure = meanArterialPressure,
            timestampYear = timestampYear,
            timestampMonth = timestampMonth,
            timestampDay = timestampDay,
            timeStampHour = timeStampHour,
            timeStampMinute = timeStampMinute,
            timeStampSecond = timeStampSecond,
            pulseRate = pulseRate,
            userId = userId,
            measurementStatus = measurementStatus
        )
    }
}