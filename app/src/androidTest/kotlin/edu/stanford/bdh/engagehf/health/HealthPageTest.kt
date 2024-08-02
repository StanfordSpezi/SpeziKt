package edu.stanford.bdh.engagehf.health

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import edu.stanford.bdh.engagehf.health.weight.WeighPage
import edu.stanford.bdh.engagehf.simulator.HealthPageSimulator
import edu.stanford.spezi.core.design.component.ComposeContentActivity
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class HealthPageTest {
    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComposeContentActivity>()

    @Before
    fun init() {
        composeTestRule.activity.setScreen {
            WeighPage()
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
        healthPage {
            assertErrorMessage("Error message")
        }
    }

    @Test
    fun `test health page health chart is displayed`() {
        healthPage {
            assertHealthChartIsDisplayed()
        }
    }

    @Test
    fun `test health page health header is displayed`() {
        healthPage {
            assertHealthHeaderIsDisplayed()
        }
    }

    @Test
    fun `test health page health progress indicator is displayed`() {
        healthPage {
            assertHealthProgressIndicatorIsDisplayed()
        }
    }

    @Test
    fun `test health page health history table is displayed`() {
        healthPage {
            assertHealthHistoryTableIsDisplayed()
        }
    }

    @Test
    fun `test health page health history text is displayed`() {
        healthPage {
            assertHealthHistoryTextIsDisplayed()
        }
    }

    @Test
    fun `test health page health history text is displayed with text`() {
        healthPage {
            assertHealthHistoryText("History")
        }
    }

    private fun healthPage(block: HealthPageSimulator.() -> Unit) {
        HealthPageSimulator(composeTestRule).apply(block)
    }
}
