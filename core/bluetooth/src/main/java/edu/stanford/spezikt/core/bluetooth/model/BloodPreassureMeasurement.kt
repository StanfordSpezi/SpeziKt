package edu.stanford.spezikt.core.bluetooth.model


data class BloodPressureMeasurement(
    val flags: Flags, // these flags define which data fields are present
    val systolic: Float, // 25-280  mmHg
    val diastolic: Float, // 25-280 mmHg
    val meanArterialPressure: Float, // 25-280 mmHg
    val timestampYear: Int, // 0 is not known; 1582-9999
    val timestampMonth: Int, // 0 is not known; 1-12
    val timestampDay: Int, // 0 is not known; 1-31
    val timeStampHour: Int, // 0-23
    val timeStampMinute: Int, // 0-59
    val timeStampSecond: Int, // 0-59
    val pulseRate: Float, // pulse rate value bpm
    val userId: Int,
    val measurementStatus: MeasurementStatus
) : Measurement() {
    companion object {
        fun fromByteArray(bytes: ByteArray): BloodPressureMeasurement {
            return interpretBloodPressureMeasurement(bytes)
        }
    }
}


data class Flags(
    val bloodPressureUnitsFlag: Boolean, // false: mmHg, true: kPa
    val timeStampFlag: Boolean, // false: no timestamp, true: timestamp present
    val pulseRateFlag: Boolean, // false: no pulse rate, true: pulse rate present
    val userIdFlag: Boolean, // false: no user ID, true: user ID present
    val measurementStatusFlag: Boolean // false: no measurement status, true: measurement status present
)

data class MeasurementStatus(
    val bodyMovementDetectionFlag: Boolean, // 0: no body movement, 1: body movement
    val cuffFitDetectionFlag: Boolean, // 0: fit properly, 1: too loose
    val irregularPulseDetectionFlag: Boolean, // 0: no irregular pulse detected, 1: irregular pulse detected
    val pulseRateRangeDetectionFlags: Int, // - always 0
    val measurementPositionDetectionFlag: Boolean // false: proper, true: improper
)

private fun interpretBloodPressureMeasurement(value: ByteArray): BloodPressureMeasurement {
    val flags = Flags(
        bloodPressureUnitsFlag = (value[0].toInt() and 0b00000001) != 0,
        timeStampFlag = (value[0].toInt() and 0b00000010) != 0,
        pulseRateFlag = (value[0].toInt() and 0b00000100) != 0,
        userIdFlag = (value[0].toInt() and 0b00001000) != 0,
        measurementStatusFlag = (value[0].toInt() and 0b00100000) != 0
    )

    val systolic = (value[1].toInt() and 0xFF).toFloat()
    val diastolic = (value[3].toInt() and 0xFF).toFloat()
    val meanArterialPressure = (value[5].toInt() and 0xFF).toFloat()
    val timestampYear = (value[7].toInt() and 0xFF)
    val timestampMonth = (value[9].toInt() and 0xFF)
    val timestampDay = (value[10].toInt() and 0xFF)
    val timeStampHour = (value[11].toInt() and 0xFF)
    val timeStampMinute = (value[12].toInt() and 0xFF)
    val timeStampSecond = (value[13].toInt() and 0xFF)
    val pulseRate = (value[14].toInt() and 0xFF).toFloat()
    val userId = (value[16].toInt() and 0xFF)

    val measurementStatus = MeasurementStatus(
        bodyMovementDetectionFlag = (value[17].toInt() and 0b0000000000000001) != 0,
        cuffFitDetectionFlag = (value[17].toInt() and 0b0000000000000010) != 0,
        irregularPulseDetectionFlag = (value[17].toInt() and 0b0000000000000100) != 0,
        pulseRateRangeDetectionFlags = (value[17].toInt() shr 3) and 0b11,
        measurementPositionDetectionFlag = (value[17].toInt() and 0b00100000) != 0
    )

    return BloodPressureMeasurement(
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