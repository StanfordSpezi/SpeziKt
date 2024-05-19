package edu.stanford.spezikt.core.bluetooth.data.model

import java.time.ZonedDateTime

sealed interface Measurement {

    data class BloodPressure(
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
        val measurementStatus: Status
    ) : Measurement {

        data class Flags(
            val bloodPressureUnitsFlag: Boolean, // false: mmHg, true: kPa
            val timeStampFlag: Boolean, // false: no timestamp, true: timestamp present
            val pulseRateFlag: Boolean, // false: no pulse rate, true: pulse rate present
            val userIdFlag: Boolean, // false: no user ID, true: user ID present
            val measurementStatusFlag: Boolean // false: no measurement status, true: measurement status present
        )

        data class Status(
            val bodyMovementDetectionFlag: Boolean, // 0: no body movement, 1: body movement
            val cuffFitDetectionFlag: Boolean, // 0: fit properly, 1: too loose
            val irregularPulseDetectionFlag: Boolean, // 0: no irregular pulse detected, 1: irregular pulse detected
            val pulseRateRangeDetectionFlags: Int, // - always 0
            val measurementPositionDetectionFlag: Boolean // false: proper, true: improper
        )
    }

    data class Weight(
        val weight: Double?,
        val zonedDateTime: ZonedDateTime?,
        val userId: Int?,
        val bmi: Double?,
        val height: Double?
    ) : Measurement
}
