package edu.stanford.spezi.core.bluetooth.data.mapper

import android.bluetooth.BluetoothGattCharacteristic
import com.google.common.truth.Truth.assertThat
import edu.stanford.spezi.core.bluetooth.data.model.Measurement
import edu.stanford.spezi.core.testing.runTestUnconfined
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import org.junit.Test
import kotlin.random.Random

class MeasurementMapperTest {
    private val weightMeasurementMapper: WeightMeasurementMapper = mockk()
    private val bloodPressureMapper: BloodPressureMapper = mockk()

    private val measurementMapper by lazy {
        MeasurementMapperImpl(
            ioDispatcher = UnconfinedTestDispatcher(),
            weightMeasurementMapper = weightMeasurementMapper,
            bloodPressureMapper = bloodPressureMapper,
        )
    }

    @Test
    fun `it should return recognise if any of the child mapper recognises the characteristic`() {
        // given
        val characteristic: BluetoothGattCharacteristic = mockk()
        val weightMapperResult = Random.nextBoolean()
        val bloodPressureMapperResult = Random.nextBoolean()
        every { weightMeasurementMapper.recognises(characteristic) } returns weightMapperResult
        every { bloodPressureMapper.recognises(characteristic) } returns bloodPressureMapperResult

        // when
        val result = measurementMapper.recognises(characteristic)

        // then
        assertThat(result).isEqualTo(weightMapperResult or bloodPressureMapperResult)
    }

    @Test
    fun `it should return the measurement of the weightMeasurementMapper`() = runTestUnconfined {
        // given
        val characteristic: BluetoothGattCharacteristic = mockk()
        val measurement: Measurement = mockk()
        val data = ByteArray(10)
        every { weightMeasurementMapper.recognises(characteristic) } returns true
        coEvery { weightMeasurementMapper.map(characteristic, data) } returns measurement
        every { bloodPressureMapper.recognises(characteristic) } returns false

        // when
        val result = measurementMapper.map(characteristic, data)

        // then
        assertThat(result).isEqualTo(measurement)
    }

    @Test
    fun `it should return the measurement of the bloodPressureMapper`() = runTestUnconfined {
        // given
        val characteristic: BluetoothGattCharacteristic = mockk()
        val measurement: Measurement = mockk()
        val data = ByteArray(10)
        every { bloodPressureMapper.recognises(characteristic) } returns true
        coEvery { bloodPressureMapper.map(characteristic, data) } returns measurement
        every { weightMeasurementMapper.recognises(characteristic) } returns false

        // when
        val result = measurementMapper.map(characteristic, data)

        // then
        assertThat(result).isEqualTo(measurement)
    }
}
