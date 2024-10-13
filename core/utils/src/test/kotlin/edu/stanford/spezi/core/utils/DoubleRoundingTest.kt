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

    @Test
    fun `roundToDecimalPlaces with 0 decimal places returns correct value`() {
        // Given
        val value = 1.2345
        val decimalPlaces = 0

        // When
        val result = value.roundToDecimalPlaces(decimalPlaces)

        // Then
        assertThat(result).isEqualTo(1.0)
    }

    @Test
    fun `roundToDecimalPlaces with 1 decimal places returns correct value`() {
        // Given
        val value = 1.2345
        val decimalPlaces = 1

        // When
        val result = value.roundToDecimalPlaces(decimalPlaces)

        // Then
        assertThat(result).isEqualTo(1.2)
    }

    @Test
    fun `roundToDecimalPlaces with 2 decimal places returns correct value`() {
        // Given
        val value = 1.2345
        val decimalPlaces = 2

        // When
        val result = value.roundToDecimalPlaces(decimalPlaces)

        // Then
        assertThat(result).isEqualTo(1.23)
    }

    @Test
    fun `roundToDecimalPlaces with 3 decimal places returns correct value`() {
        // Given
        val value = 1.2345
        val decimalPlaces = 3

        // When
        val result = value.roundToDecimalPlaces(decimalPlaces)

        // Then
        assertThat(result).isEqualTo(1.235)
    }
}
