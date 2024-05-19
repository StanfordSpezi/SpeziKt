package edu.stanford.spezikt.core.bluetooth.data.mapper

import android.bluetooth.BluetoothGattCharacteristic
import edu.stanford.spezikt.core.bluetooth.data.model.Measurement
import edu.stanford.spezikt.coroutines.di.Dispatching
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Component for mapping Bluetooth Low Energy (BLE) characteristics to measurements.
 */
internal interface MeasurementMapper {

    /**
     * Determines if the given Bluetooth GATT characteristic is recognized by the mapper.
     *
     * @param characteristic The Bluetooth GATT characteristic to be recognized.
     * @return true if the characteristic is recognized, false otherwise.
     */
    fun recognises(characteristic: BluetoothGattCharacteristic?): Boolean

    /**
     * Maps a Bluetooth GATT characteristic and its data to a measurement.
     *
     * @param characteristic The Bluetooth GATT characteristic to be mapped.
     * @param data The byte array representing the data of the characteristic.
     * @return The mapped measurement, or null if the mapping was unsuccessful.
     */
    suspend fun map(characteristic: BluetoothGattCharacteristic?, data: ByteArray): Measurement?

    /**
     * Nested interface representing a child measurement mapper.
     *
     * This interface extends the MeasurementMapper interface and can be used to define specialized
     * measurement mappers for specific types of measurements.
     */
    interface Child : MeasurementMapper
}

/**
 * Implementation of the MeasurementMapper interface.
 *
 * @property ioDispatcher The IO coroutine dispatcher used for the mapping operation.
 * @param weightMeasurementMapper The mapper for weight measurements.
 * @param bloodPressureMapper The mapper for blood pressure measurements.
 */
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