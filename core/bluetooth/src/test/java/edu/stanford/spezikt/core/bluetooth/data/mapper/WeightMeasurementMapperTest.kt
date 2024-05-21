package edu.stanford.spezikt.core.bluetooth.data.mapper

import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattService
import com.google.common.truth.Truth.assertThat
import edu.stanford.spezi.core.testing.runTestUnconfined
import edu.stanford.spezikt.core.bluetooth.data.model.BLEServiceType
import edu.stanford.spezikt.core.bluetooth.data.model.Measurement
import io.mockk.every
import io.mockk.mockk
import org.junit.Test
import java.util.UUID

class WeightMeasurementMapperTest {
    private val serviceType = BLEServiceType.WEIGHT
    private val mapper = WeightMeasurementMapper()
    private val service: BluetoothGattService = mockk {
        every { uuid } returns serviceType.service
    }
    private val characteristic: BluetoothGattCharacteristic = mockk {
        every { service } returns this@WeightMeasurementMapperTest.service
        every { uuid } returns serviceType.characteristic
    }

    @Test
    fun `it should recognise characteristic with WEIGHT values`() {
        // given
        val sut = mapper

        // when
        val result = sut.recognises(characteristic)

        // then
        assertThat(result).isTrue()
    }

    @Test
    fun `it should recognise characteristic with unknown values`() {
        // given
        every { characteristic.uuid } returns UUID.randomUUID()
        val sut = mapper

        // when
        val result = sut.recognises(characteristic)

        // then
        assertThat(result).isFalse()
    }

    @Test
    fun `it should map characteristic and data to weight measurement`() = runTestUnconfined {
        // given
        val sut = mapper
        val data = byteArrayOf(
            0b00000001,
            0x30,
            0x75,
        )

        // when
        val result = sut.map(characteristic, data)

        // then
        assertThat(result).isInstanceOf(Measurement.Weight::class.java)
    }

    @Test
    fun `it should return null when characteristic is not recognised`() = runTestUnconfined {
        // given
        val sut = mapper
        every { characteristic.uuid } returns UUID.randomUUID()
        val data = byteArrayOf()

        // when
        val result = sut.map(characteristic, data)

        // then
        assertThat(result).isNull()
    }

    @Test
    fun `it should return null when mapping fails`() = runTestUnconfined {
        // given
        val sut = mapper
        val data = byteArrayOf(0x00)

        // when
        val result = sut.map(characteristic, data)

        // then
        assertThat(result).isNull()
    }
}