package edu.stanford.bdh.engagehf.health

import androidx.health.connect.client.records.Record
import androidx.health.connect.client.records.WeightRecord
import androidx.health.connect.client.units.Mass
import com.google.common.truth.Truth.assertThat
import edu.stanford.bdh.engagehf.R
import edu.stanford.spezi.core.design.component.StringResource
import edu.stanford.spezi.core.utils.LocaleProvider
import edu.stanford.spezi.core.utils.extensions.roundToDecimalPlaces
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
    private val zonedDateTime = ZonedDateTime.now()

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
    fun `mapToHealthData with precise data will round the value to at most 2 decimal places`() {
        // Given
        val weightInKg = 70.123456789
        val records = listOf(
            createWeightRecord(
                weightInKg = weightInKg,
            ),
        )

        // When
        val result = healthUiStateMapper.map(records)

        // Then
        assertThat(result.chartData).isNotEmpty()
        assertThat(
            result.chartData[0].xValues[0].toBigDecimal().stripTrailingZeros().scale()
        ).isAtMost(2)
        assertThat(result.chartData[0].yValues[0]).isEqualTo(154.60)
    }

    @Test
    fun `mapToHealthData with non-empty records and selectedTimeRange WEEKLY returns correct HealthUiData`() {
        // Given
        val records = listOf(
            createWeightRecord(
                year = zonedDateTime.year,
                day = 1,
            ),
            createWeightRecord(
                year = zonedDateTime.year,
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
    fun `it should map delete record alert data correctly`() {
        // given
        val recordId = "some-record-id"
        val action = HealthAction.RequestDeleteRecord(recordId = recordId)

        // when
        val result = healthUiStateMapper.mapDeleteRecordAlertData(action)

        // then
        assertThat(result).isEqualTo(
            DeleteRecordAlertData(
                recordId = recordId,
                title = StringResource(R.string.delete_health_record),
                description = StringResource(R.string.health_record_deletion_description),
                confirmButton = StringResource(R.string.confirm_button_text),
                dismissButton = StringResource(R.string.dismiss_button_text),
            )
        )
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
                year = zonedDateTime.year + 1,
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

    @Test
    fun `it should map x and y values and format correctly for selectedTimeRange DAILY`() {
        // Given
        val records = List(5) {
            createWeightRecord(
                day = it + 1,
                weightInKg = 70.0 + it
            )
        }
        val expectedYValues = records.map { it.weight.inPounds.roundToDecimalPlaces(2) }

        // When
        val result = healthUiStateMapper.map(records, TimeRange.DAILY)
        val chartData = result.chartData.first()

        // Then
        assertThat(chartData.yValues).isEqualTo(expectedYValues)
        assertThat(chartData.xValues).isEqualTo(List(5) { it.toDouble() })
        chartData.xValues.forEach {
            assertThat(result.valueFormatter(it)).matches(monthRegex(it.toInt() + 1))
        }
    }

    @Test
    fun `it should map x and y values and format correctly for selectedTimeRange WEEKLY`() {
        // Given
        val records = List(3) {
            createWeightRecord(
                day = 1 + (it * 7),
                weightInKg = 70.0 + it
            )
        }
        val expectedYValues = records.map { it.weight.inPounds.roundToDecimalPlaces(2) }

        // When
        val result = healthUiStateMapper.map(records, TimeRange.WEEKLY)
        val chartData = result.chartData.first()

        // Then
        assertThat(chartData.yValues).isEqualTo(expectedYValues)
        assertThat(chartData.xValues).isEqualTo(List(3) { it.toDouble() })
        assertThat(result.xValue(0)).matches(monthRegex(1))
        assertThat(result.xValue(1)).matches(monthRegex(8))
        assertThat(result.xValue(2)).matches(monthRegex(15))
    }

    @Test
    fun `it should map x and y values and format correctly for selectedTimeRange MONTHLY`() {
        // Given
        val records = List(3) {
            createWeightRecord(
                month = 1 + it,
                day = 1,
                weightInKg = 70.0 + it
            )
        }
        val year = zonedDateTime.year % 100
        val expectedYValues = records.map { it.weight.inPounds.roundToDecimalPlaces(2) }

        // When
        val result = healthUiStateMapper.map(records, TimeRange.MONTHLY)
        val chartData = result.chartData.first()

        // Then
        assertThat(chartData.yValues).isEqualTo(expectedYValues)
        assertThat(chartData.xValues).isEqualTo(List(3) { it.toDouble() })
        assertThat(result.xValue(0)).isEqualTo("Jan $year")
        assertThat(result.xValue(1)).isEqualTo("Feb $year")
        assertThat(result.xValue(2)).isEqualTo("Mar $year")
    }

    private fun createWeightRecord(
        year: Int = zonedDateTime.year,
        month: Int = zonedDateTime.monthValue,
        day: Int = zonedDateTime.dayOfMonth,
        weightInKg: Double = 70.0,
    ): WeightRecord {
        return WeightRecord(
            time = ZonedDateTime.of(
                year,
                month,
                day,
                0,
                0,
                0,
                0,
                ZoneId.systemDefault()
            ).toInstant(),
            zoneOffset = zonedDateTime.offset,
            weight = Mass.kilograms(weightInKg)
        )
    }

    private fun monthRegex(day: Int): String {
        val dayString = if (day < 10) "0$day" else "$day"
        return "^[A-Za-z]{3} $dayString"
    }

    private fun HealthUiStateMapper.map(
        records: List<Record>,
        timeRange: TimeRange = TimeRange.DAILY,
    ) = (mapToHealthData(records, timeRange) as HealthUiState.Success).data

    private fun HealthUiData.xValue(index: Int) = valueFormatter(chartData.first().xValues[index])
}
