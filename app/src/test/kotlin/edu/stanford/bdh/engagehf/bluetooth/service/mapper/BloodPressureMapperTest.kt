package edu.stanford.bdh.engagehf.bluetooth.service.mapper

import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattService
import com.google.common.truth.Truth.assertThat
import edu.stanford.bdh.engagehf.bluetooth.service.BLEServiceType
import edu.stanford.bdh.engagehf.bluetooth.service.Measurement
import edu.stanford.spezi.modules.testing.runTestUnconfined
import io.mockk.every
import io.mockk.mockk
import org.junit.Test
import java.util.UUID

class BloodPressureMapperTest {
    private val serviceType = BLEServiceType.BLOOD_PRESSURE
    private val mapper = BloodPressureMapper()
    private val service: BluetoothGattService = mockk {
        every { uuid } returns serviceType.service
    }
    private val characteristic: BluetoothGattCharacteristic = mockk {
        every { service } returns this@BloodPressureMapperTest.service
        every { uuid } returns serviceType.characteristic
    }

    @Test
    fun `it should recognise characteristic with BLOOD_PRESSURE values`() {
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
    fun `it should map characteristic and data to blood pressure measurement`() =
        runTestUnconfined {
            // given
            val sut = mapper
            val data = byteArrayOf(
                0b00101111, // Flags: bloodPressureUnitsFlag, timeStampFlag, pulseRateFlag, userIdFlag, measurementStatusFlag
                120, // Systolic
                0,
                80, // Diastolic
                0,
                70, // Mean Arterial Pressure
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
            )

            // when
            val result = sut.map(characteristic, data) as Measurement.BloodPressure

            // then
            with(result) {
                val expectedFlags = Measurement.BloodPressure.Flags(
                    bloodPressureUnitsFlag = true,
                    timeStampFlag = true,
                    pulseRateFlag = true,
                    userIdFlag = true,
                    measurementStatusFlag = true
                )
                assertThat(flags).isEqualTo(expectedFlags)
                assertThat(systolic).isEqualTo(120.0f)
                assertThat(diastolic).isEqualTo(80.0f)
                assertThat(meanArterialPressure).isEqualTo(70.0f)
            }
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

    @Test
    fun `it should return correct date when map from byteArray`() = runTestUnconfined {
        // given
        val data = byteArrayOf(30, -126, 0, 83, 0, 98, 0, -24, 7, 9, 30, 22, 11, 23, 80, 0, 1, 0, 0)

        // when
        val result = mapper.map(characteristic, data) as Measurement.BloodPressure

        // then
        with(result) {
            assertThat(timestampYear).isEqualTo(2024)
            assertThat(timestampMonth).isEqualTo(9)
            assertThat(timestampDay).isEqualTo(30)
            assertThat(timeStampHour).isEqualTo(22)
            assertThat(timeStampMinute).isEqualTo(11)
            assertThat(timeStampSecond).isEqualTo(23)
        }
    }
}
