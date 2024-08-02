package edu.stanford.bdh.engagehf.health

import androidx.compose.runtime.collectAsState
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.health.connect.client.records.WeightRecord
import androidx.health.connect.client.units.Mass
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import edu.stanford.bdh.engagehf.health.components.HealthHeaderData
import edu.stanford.bdh.engagehf.health.weight.WeightViewModel
import edu.stanford.bdh.engagehf.simulator.HealthPageSimulator
import edu.stanford.spezi.core.design.component.ComposeContentActivity
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.ZonedDateTime

@HiltAndroidTest
class HealthPageTest {
    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComposeContentActivity>()

    private lateinit var viewModel: WeightViewModel

    private val uiStateFlow = MutableStateFlow<HealthUiState>(HealthUiState.Loading)

    @Before
    fun setup() {
        viewModel = mockk(relaxed = true)
        every { viewModel.uiState } returns uiStateFlow
        composeTestRule.activity.setScreen {
            HealthPage(
                uiState = viewModel.uiState.collectAsState().value,
                onAction = { viewModel::onAction })
        }
    }

    @Test
    fun `test health page root is displayed`() {
        healthPage {
            assertIsDisplayed()
        }
    }

    @Test
    fun `test health page error message is displayed`() {
        // given
        uiStateFlow.value = HealthUiState.Error("Error message")
        // then
        healthPage {
            assertErrorMessage("Error message")
        }
    }

    @Test
    fun `test health page health chart is displayed`() {
        // given
        uiStateFlow.value = getSuccessState()
        // then
        healthPage {
            assertHealthChartIsDisplayed()
        }
    }

    @Test
    fun `test health page health header is displayed`() {
        // given
        uiStateFlow.value = getSuccessState()
        // then
        healthPage {
            assertHealthHeaderIsDisplayed()
        }
    }

    @Test
    fun `test health page health progress indicator is displayed`() {
        // given
        uiStateFlow.value = HealthUiState.Loading
        // then
        healthPage {
            assertHealthProgressIndicatorIsDisplayed()
        }
    }

    @Test
    fun `test health page health history table is displayed`() {
        // given
        uiStateFlow.value = getSuccessState()
        // then
        healthPage {
            assertHealthHistoryTableIsDisplayed()
        }
    }

    @Test
    fun `test health page health history text is displayed`() {
        // given
        uiStateFlow.value = getSuccessState()
        // then
        healthPage {
            assertHealthHistoryTextIsDisplayed()
        }
    }

    @Test
    fun `test health page health history text is displayed with text`() {
        // given
        uiStateFlow.value = getSuccessState()
        // then
        healthPage {
            assertHealthHistoryText("History")
        }
    }

    private fun healthPage(block: HealthPageSimulator.() -> Unit) {
        HealthPageSimulator(composeTestRule).apply(block)
    }

    private fun getSuccessState(): HealthUiState {
        return HealthUiState.Success(
            data = HealthUiData(
                headerData = HealthHeaderData(
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
                        id = null
                    )
                )
            )
        )
    }
}
