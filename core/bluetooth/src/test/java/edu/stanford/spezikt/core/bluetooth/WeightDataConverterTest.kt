package edu.stanford.spezikt.core.bluetooth

import edu.stanford.spezikt.core.bluetooth.model.WeightMeasurement
import junit.framework.TestCase.assertEquals
import org.junit.Test


class WeightDataConverterTest {


    @Test
    fun convert() {
        // 23,2 kg   user 1
        var data = byteArrayOf(6, 32, 18, 0, 0, 0, 0, 0, 0, 0, 1)
        var result = WeightMeasurement.fromByteArray(data)
        println(result)
        assertEquals(23.2, result.weight)

        // 18.0 kg
        data = byteArrayOf(6, 16, 14, 0, 0, 0, 0, 0, 0, 0, 1)
        result = WeightMeasurement.fromByteArray(data)
        assertEquals(18.0, result.weight)
    }
}