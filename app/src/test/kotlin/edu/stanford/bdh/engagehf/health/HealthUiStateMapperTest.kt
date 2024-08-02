package edu.stanford.bdh.engagehf.health

import androidx.health.connect.client.records.WeightRecord
import androidx.health.connect.client.units.Mass
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test
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
            WeightRecord(
                time = ZonedDateTime.now().minusDays(1).toInstant(),
                zoneOffset = ZonedDateTime.now().offset,
                weight = Mass.kilograms(70.0)
            ),
            WeightRecord(
                time = ZonedDateTime.now().toInstant(),
                zoneOffset = ZonedDateTime.now().offset,
                weight = Mass.kilograms(72.0)
            )
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
}
