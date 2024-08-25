package edu.stanford.spezi.core.utils

import com.google.common.truth.Truth.assertThat
import edu.stanford.spezi.core.utils.extensions.roundToDecimalPlaces
import org.junit.Test

class FloatRoundingTest {

    @Test
    fun `it should round correctly`() {
        // given
        val float = 1.111111f
        val places = 3

        // when
        val result = float.roundToDecimalPlaces(places = places)
        val decimalPlaces = "$result".split(".").last()

        // then
        assertThat(decimalPlaces.length).isEqualTo(places)
    }
}
