package edu.stanford.spezi.core.utils

import com.google.common.truth.Truth.assertThat
import edu.stanford.spezi.core.utils.extensions.roundToDecimalPlaces
import org.junit.Test

class DoubleRoundingTest {

    @Test
    fun `it should round correctly`() {
        // given
        val double = 1.111111
        val places = 3

        // when
        val result = double.roundToDecimalPlaces(places = places)
        val decimalPlaces = "$result".split(".").last()

        // then
        assertThat(decimalPlaces.length).isEqualTo(places)
    }
}
