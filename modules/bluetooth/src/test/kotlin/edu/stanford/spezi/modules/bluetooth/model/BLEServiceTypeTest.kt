package edu.stanford.spezi.modules.bluetooth.model

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class BLEServiceTypeTest {

    @Test
    fun `assert WEIGHT type has correct values`() {
        // given
        val weight = BLEServiceType.WEIGHT

        // when
        val service = weight.service
        val characteristic = weight.characteristic

        // then
        assertThat(service.toString()).isEqualTo("0000181d-0000-1000-8000-00805f9b34fb")
        assertThat(characteristic.toString()).isEqualTo("00002a9d-0000-1000-8000-00805f9b34fb")
    }

    @Test
    fun `assert BLOOD_PRESSURE type has correct values`() {
        // given
        val bloodPressure = BLEServiceType.BLOOD_PRESSURE

        // when
        val service = bloodPressure.service
        val characteristic = bloodPressure.characteristic

        // then
        assertThat(service.toString()).isEqualTo("00001810-0000-1000-8000-00805f9b34fb")
        assertThat(characteristic.toString()).isEqualTo("00002a35-0000-1000-8000-00805f9b34fb")
    }
}
