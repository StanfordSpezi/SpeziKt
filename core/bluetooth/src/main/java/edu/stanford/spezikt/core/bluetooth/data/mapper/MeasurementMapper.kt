package edu.stanford.spezikt.core.bluetooth.data.mapper

import android.bluetooth.BluetoothGattCharacteristic
import edu.stanford.spezikt.core.bluetooth.data.model.Measurement
import edu.stanford.spezikt.coroutines.di.Dispatching
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal interface MeasurementMapper {
    fun recognises(characteristic: BluetoothGattCharacteristic?): Boolean
    suspend fun map(characteristic: BluetoothGattCharacteristic?, data: ByteArray): Measurement?

    interface Child : MeasurementMapper
}

internal class MeasurementMapperImpl @Inject constructor(
    @Dispatching.IO private val ioDispatcher: CoroutineDispatcher,
    weightMeasurementMapper: WeightMeasurementMapper,
    bloodPressureMapper: BloodPressureMapper,
) : MeasurementMapper {
    private val children = listOf(
        weightMeasurementMapper,
        bloodPressureMapper,
    )

    override fun recognises(characteristic: BluetoothGattCharacteristic?): Boolean =
        children.any { it.recognises(characteristic) }

    override suspend fun map(characteristic: BluetoothGattCharacteristic?, data: ByteArray): Measurement? = withContext(ioDispatcher) {
        children.find { it.recognises(characteristic) }?.map(characteristic, data)
    }
}