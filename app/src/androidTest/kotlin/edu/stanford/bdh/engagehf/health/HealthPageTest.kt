package edu.stanford.bdh.engagehf.health

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.health.connect.client.records.WeightRecord
import androidx.health.connect.client.units.Mass
import edu.stanford.bdh.engagehf.simulator.HealthPageSimulator
import org.junit.Rule
import org.junit.Test
import java.time.ZonedDateTime

class HealthPageTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `test health page root is displayed`() {
        // given
        setState(state = getSuccessState())

        // then
        healthPage {
            assertIsDisplayed()
        }
    }

    @Test
    fun `test health page error message is displayed`() {
        // given
        val message = "Error message"

        // when
        setState(state = HealthUiState.Error(message))

        // then
        healthPage {
            assertErrorMessage(message)
            assertCenteredContent()
        }
    }

    @Test
    fun `test health page no data message is displayed`() {
        // given
        val message = "No data available"

        // when
        setState(state = HealthUiState.NoData(message))

        // then
        healthPage {
            assertNoDataMessage(message)
            assertCenteredContent()
        }
    }

    @Test
    fun `test health page health chart is displayed`() {
        // given
        setState(state = getSuccessState())

        // then
        healthPage {
            assertHealthChartIsDisplayed()
        }
    }

    @Test
    fun `test health page health header is displayed`() {
        // given
        setState(state = getSuccessState())
        // then
        healthPage {
            assertHealthHeaderIsDisplayed()
        }
    }

    @Test
    fun `test health page health progress indicator is displayed`() {
        // given
        setState(state = HealthUiState.Loading)
        // then
        healthPage {
            assertHealthProgressIndicatorIsDisplayed()
            assertCenteredContent()
        }
    }

    @Test
    fun `test health page health history table is displayed`() {
        // given
        val entryId = "entry-id"

        // when
        setState(state = getSuccessState(entryId = entryId))

        // then
        healthPage {
            assertHistoryTableItemDisplayed(id = entryId)
        }
    }

    @Test
    fun `test health page health history text is displayed`() {
        // given
        setState(state = getSuccessState())
        // then
        healthPage {
            assertHealthHistoryTextIsDisplayed()
        }
    }

    @Test
    fun `test health page health history text is displayed with text`() {
        // given
        setState(state = getSuccessState())
        // then
        healthPage {
            assertHealthHistoryText("History")
        }
    }

    private fun setState(state: HealthUiState) {
        composeTestRule.setContent {
            HealthPage(uiState = state, onAction = {})
        }
    }

    private fun healthPage(block: HealthPageSimulator.() -> Unit) {
        HealthPageSimulator(composeTestRule).apply(block)
    }

    private fun getSuccessState(entryId: String? = null): HealthUiState {
        return HealthUiState.Success(
            data = HealthUiData(
                infoRowData = InfoRowData(
                    selectedTimeRange = TimeRange.MONTHLY,
                    formattedValue = "70.0 kg",
                    formattedDate = "Jan 2022",
                    isSelectedTimeRangeDropdownExpanded = false
                ),
                records = listOf(
                    WeightRecord(
                        time = ZonedDateTime.now().toInstant(),
                        zoneOffset = ZonedDateTime.now().offset,
                        weight = @Suppress("MagicNumber") Mass.pounds(154.0)
                    )
                ),
                tableData = listOf(
                    TableEntryData(
                        value = 70.0f,
                        formattedValues = "70.0 kg",
                        date = ZonedDateTime.now(),
                        formattedDate = "Jan 2022",
                        trend = 0f,
                        formattedTrend = "0.0 kg",
                        secondValue = null,
                        id = entryId
                    )
                )
            )
        )
    }
}
