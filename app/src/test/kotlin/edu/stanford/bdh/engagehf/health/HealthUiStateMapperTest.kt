package edu.stanford.bdh.engagehf.health

import androidx.health.connect.client.records.Record
import androidx.health.connect.client.records.WeightRecord
import androidx.health.connect.client.units.Mass
import com.google.common.truth.Truth.assertThat
import edu.stanford.spezi.core.utils.LocaleProvider
import io.mockk.every
import io.mockk.mockk
import org.junit.Before
import org.junit.Test
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.Locale

class HealthUiStateMapperTest {

    private val localeProvider: LocaleProvider = mockk()
    private val healthUiStateMapper = HealthUiStateMapper(localeProvider = localeProvider)

    @Before
    fun setup() {
        every { localeProvider.getDefaultLocale() } returns Locale.US
    }

    @Test
    fun `mapToHealthData with empty records returns empty HealthUiData`() {
        // Given
        val records = emptyList<WeightRecord>()
        val selectedTimeRange = TimeRange.DAILY

        // When
        val result = healthUiStateMapper.mapToHealthData(records, selectedTimeRange)

        // Then
        assertThat(result).isInstanceOf(HealthUiState.NoData::class.java)
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

        // When
        val result = healthUiStateMapper.map(records)

        // Then
        assertThat(result.records).isNotEmpty()
        assertThat(result.chartData).isNotEmpty()
        assertThat(result.tableData).isNotEmpty()
        assertThat(result.newestData).isNotNull()
        assertThat(result.infoRowData.formattedValue).isNotEmpty()
        assertThat(result.infoRowData.formattedDate).isNotEmpty()
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

        // When
        val result = healthUiStateMapper.map(records)

        // Then
        assertThat(result.records).isNotEmpty()
        assertThat(result.chartData).isNotEmpty()
        assertThat(result.chartData.size).isEqualTo(1)
        assertThat(result.tableData).isNotEmpty()
        assertThat(result.tableData.size).isEqualTo(2)
        assertThat(result.newestData).isNotNull()
        assertThat(result.infoRowData.formattedValue).isNotEmpty()
        assertThat(result.infoRowData.formattedDate).isNotEmpty()
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

        // When
        val result = healthUiStateMapper.map(records, TimeRange.MONTHLY)

        // Then
        assertThat(result.records).isNotEmpty()
        assertThat(result.chartData).isNotEmpty()
        assertThat(result.tableData).isNotEmpty()
        assertThat(result.tableData.size).isEqualTo(3)
        assertThat(result.chartData.size).isEqualTo(1)
        assertThat(result.chartData[0].xValues.size).isEqualTo(2)
        assertThat(result.chartData[0].yValues.size).isEqualTo(2)
        assertThat(result.newestData).isNotNull()
        assertThat(result.infoRowData.formattedValue).isNotEmpty()
        assertThat(result.infoRowData.formattedDate).isNotEmpty()
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

    private fun HealthUiStateMapper.map(records: List<Record>, timeRange: TimeRange = TimeRange.DAILY) =
        (mapToHealthData(records, timeRange) as HealthUiState.Success).data
}
