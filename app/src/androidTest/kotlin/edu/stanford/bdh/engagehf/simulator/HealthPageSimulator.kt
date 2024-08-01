package edu.stanford.bdh.engagehf.simulator

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.ComposeTestRule
import edu.stanford.bdh.engagehf.health.HealthPageTestIdentifier
import edu.stanford.spezi.core.testing.onNodeWithIdentifier

class HealthPageSimulator(
    composeTestRule: ComposeTestRule,
) {
    private val root = composeTestRule.onNodeWithIdentifier(HealthPageTestIdentifier.ROOT)

    private val errorMessage =
        composeTestRule.onNodeWithIdentifier(HealthPageTestIdentifier.ERROR_MESSAGE)

    private val healthChart =
        composeTestRule.onNodeWithIdentifier(HealthPageTestIdentifier.HEALTH_CHART)

    private val healthHeader =
        composeTestRule.onNodeWithIdentifier(HealthPageTestIdentifier.HEALTH_HEADER)

    private val healthProgressIndicator =
        composeTestRule.onNodeWithIdentifier(HealthPageTestIdentifier.PROGRESS_INDICATOR)

    private val healthHistoryTable =
        composeTestRule.onNodeWithIdentifier(HealthPageTestIdentifier.HEALTH_HISTORY_TABLE)

    private val healthHistoryText =
        composeTestRule.onNodeWithIdentifier(HealthPageTestIdentifier.HEALTH_HISTORY_TEXT)

    fun assertIsDisplayed() {
        root.assertIsDisplayed()
    }

    fun assertErrorMessage(text: String) {
        errorMessage.assertIsDisplayed().assertTextEquals(text)
    }

    fun assertHealthChartIsDisplayed() {
        healthChart.assertIsDisplayed()
    }

    fun assertHealthHeaderIsDisplayed() {
        healthHeader.assertIsDisplayed()
    }

    fun assertHealthProgressIndicatorIsDisplayed() {
        healthProgressIndicator.assertIsDisplayed()
    }

    fun assertHealthHistoryTableIsDisplayed() {
        healthHistoryTable.assertIsDisplayed()
    }

    fun assertHealthHistoryTextIsDisplayed() {
        healthHistoryText.assertIsDisplayed()
    }

    fun assertHealthHistoryText(text: String) {
        healthHistoryText.assertIsDisplayed().assertTextEquals(text)
    }
}
