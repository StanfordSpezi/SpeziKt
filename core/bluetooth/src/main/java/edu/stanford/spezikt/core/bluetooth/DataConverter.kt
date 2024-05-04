package edu.stanford.spezikt.core.bluetooth

import edu.stanford.spezikt.core.bluetooth.model.BloodPressureMeasurement
import edu.stanford.spezikt.core.bluetooth.model.WeightMeasurement

fun interface DataConverter<T> {
    fun convert(data: ByteArray): T
}

class BloodPressureDataConverter : DataConverter<BloodPressureMeasurement> {
    override fun convert(data: ByteArray): BloodPressureMeasurement {
        return BloodPressureMeasurement.fromByteArray(data)
    }
}

class WeightDataConverter : DataConverter<WeightMeasurement> {
    override fun convert(data: ByteArray): WeightMeasurement {
        return WeightMeasurement.fromByteArray(data)
    }
}