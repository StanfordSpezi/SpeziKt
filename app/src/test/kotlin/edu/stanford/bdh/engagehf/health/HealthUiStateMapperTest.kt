package edu.stanford.bdh.engagehf.health

import androidx.health.connect.client.records.WeightRecord
import androidx.health.connect.client.units.Mass
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test
import java.time.ZoneId
import java.time.ZonedDateTime

class HealthUiStateMapperTest {

    private lateinit var healthUiStateMapper: HealthUiStateMapper<WeightRecord>

    @Before
    fun setup() {
        healthUiStateMapper = HealthUiStateMapper(WeightRecord::class.java)
    }

    @Test
    fun `mapToHealthData with empty records returns empty HealthUiData`() {
        // Given
        val records = emptyList<WeightRecord>()
        val selectedTimeRange = TimeRange.DAILY

        // When
        val result = healthUiStateMapper.mapToHealthData(records, selectedTimeRange)

        // Then
        assertThat(result.records).isEmpty()
        assertThat(result.chartData).isEmpty()
        assertThat(result.tableData).isEmpty()
        assertThat(result.newestData).isNull()
        assertThat(result.headerData.formattedValue).isEmpty()
        assertThat(result.headerData.formattedDate).isEmpty()
    }

    @Test
    fun `mapToHealthData with non-empty records returns correct HealthUiData`() {
        // Given
        val records = listOf(
            createWeightRecord(
                day = 1,
            ),
            createWeightRecord(
                day = 2,
            ),
        )
        val selectedTimeRange = TimeRange.DAILY

        // When
        val result = healthUiStateMapper.mapToHealthData(records, selectedTimeRange)

        // Then
        assertThat(result.records).isNotEmpty()
        assertThat(result.chartData).isNotEmpty()
        assertThat(result.tableData).isNotEmpty()
        assertThat(result.newestData).isNotNull()
        assertThat(result.headerData.formattedValue).isNotEmpty()
        assertThat(result.headerData.formattedDate).isNotEmpty()
    }

    @Test
    fun `mapToHealthData with non-empty records and selectedTimeRange WEEKLY returns correct HealthUiData`() {
        // Given
        val records = listOf(
            createWeightRecord(
                year = 2024,
                day = 1,
            ),
            createWeightRecord(
                year = 2024,
                day = 1,
            ),
        )
        val selectedTimeRange = TimeRange.WEEKLY

        // When
        val result = healthUiStateMapper.mapToHealthData(records, selectedTimeRange)

        // Then
        assertThat(result.records).isNotEmpty()
        assertThat(result.chartData).isNotEmpty()
        assertThat(result.chartData.size).isEqualTo(1)
        assertThat(result.tableData).isNotEmpty()
        assertThat(result.tableData.size).isEqualTo(2)
        assertThat(result.newestData).isNotNull()
        assertThat(result.headerData.formattedValue).isNotEmpty()
        assertThat(result.headerData.formattedDate).isNotEmpty()
    }

    @Test
    fun `mapToHealthData with non-empty records and selectedTimeRange MONTHLY returns correct HealthUiData`() {
        // Given
        val records = listOf(
            createWeightRecord(
                day = 3,
            ),
            createWeightRecord(
                day = 4,
            ),
            createWeightRecord(
                month = 9,
                day = 5,
            ),
        )
        val selectedTimeRange = TimeRange.MONTHLY

        // When
        val result = healthUiStateMapper.mapToHealthData(records, selectedTimeRange)

        // Then
        assertThat(result.records).isNotEmpty()
        assertThat(result.chartData).isNotEmpty()
        assertThat(result.tableData).isNotEmpty()
        assertThat(result.tableData.size).isEqualTo(3)
        assertThat(result.chartData.size).isEqualTo(1)
        assertThat(result.chartData[0].xValues.size).isEqualTo(2)
        assertThat(result.chartData[0].yValues.size).isEqualTo(2)
        assertThat(result.newestData).isNotNull()
        assertThat(result.headerData.formattedValue).isNotEmpty()
        assertThat(result.headerData.formattedDate).isNotEmpty()
    }

    private fun createWeightRecord(
        year: Int = 2024,
        month: Int = 8,
        day: Int = 1,
        hour: Int = 5,
        minute: Int = 4,
        second: Int = 0,
        nanoOfSecond: Int = 0,
        weightInKg: Double = 70.0,
    ): WeightRecord {
        return WeightRecord(
            time = ZonedDateTime.of(
                year,
                month,
                day,
                hour,
                minute,
                second,
                nanoOfSecond,
                ZoneId.systemDefault()
            ).toInstant(),
            zoneOffset = ZonedDateTime.now().offset,
            weight = Mass.kilograms(weightInKg)
        )
    }
}
