package edu.stanford.spezi.core.utils.extensions

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class DoubleKtTest {

    @Test
    fun `roundToDecimalPlaces with 0 decimal places returns correct value`() {
        // Given
        val value = 1.2345
        val decimalPlaces = 0

        // When
        val result = value.roundToDecimalPlaces(decimalPlaces)

        // Then
        assertThat(result).isEqualTo(1.0f)
    }

    @Test
    fun `roundToDecimalPlaces with 1 decimal places returns correct value`() {
        // Given
        val value = 1.2345
        val decimalPlaces = 1

        // When
        val result = value.roundToDecimalPlaces(decimalPlaces)

        // Then
        assertThat(result).isEqualTo(1.2f)
    }

    @Test
    fun `roundToDecimalPlaces with 2 decimal places returns correct value`() {
        // Given
        val value = 1.2345
        val decimalPlaces = 2

        // When
        val result = value.roundToDecimalPlaces(decimalPlaces)

        // Then
        assertThat(result).isEqualTo(1.23f)
    }

    @Test
    fun `roundToDecimalPlaces with 3 decimal places returns correct value`() {
        // Given
        val value = 1.2345
        val decimalPlaces = 3

        // When
        val result = value.roundToDecimalPlaces(decimalPlaces)

        // Then
        assertThat(result).isEqualTo(1.235f)
    }
}
