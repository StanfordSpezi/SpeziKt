package edu.stanford.bdh.engagehf.simulator

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.ComposeTestRule
import edu.stanford.bdh.engagehf.health.HealthPageTestIdentifier
import edu.stanford.spezi.core.design.component.CenteredBoxContentTestIdentifier
import edu.stanford.spezi.spezi.ui.helpers.testing.onNodeWithIdentifier

class HealthPageSimulator(
    private val composeTestRule: ComposeTestRule,
) {
    private val root = composeTestRule.onNodeWithIdentifier(HealthPageTestIdentifier.ROOT)

    private val centeredContent =
        composeTestRule.onNodeWithIdentifier(CenteredBoxContentTestIdentifier.ROOT)

    private val errorMessage =
        composeTestRule.onNodeWithIdentifier(HealthPageTestIdentifier.ERROR_MESSAGE)

    private val noDataMessage =
        composeTestRule.onNodeWithIdentifier(HealthPageTestIdentifier.NO_DATA_MESSAGE)

    private val healthChart =
        composeTestRule.onNodeWithIdentifier(HealthPageTestIdentifier.HEALTH_CHART)

    private val healthHeader =
        composeTestRule.onNodeWithIdentifier(HealthPageTestIdentifier.HEALTH_HEADER)

    private val healthProgressIndicator =
        composeTestRule.onNodeWithIdentifier(HealthPageTestIdentifier.PROGRESS_INDICATOR)

    private val healthHistoryText =
        composeTestRule.onNodeWithIdentifier(HealthPageTestIdentifier.HEALTH_HISTORY_TEXT)

    fun assertIsDisplayed() {
        root.assertIsDisplayed()
    }

    fun assertErrorMessage(text: String) {
        errorMessage.assertIsDisplayed().assertTextEquals(text)
    }

    fun assertNoDataMessage(text: String) {
        noDataMessage.assertIsDisplayed().assertTextEquals(text)
    }

    fun assertCenteredContent() {
        centeredContent.assertIsDisplayed()
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

    fun assertHistoryTableItemDisplayed(id: String) {
        composeTestRule.onNodeWithIdentifier(
            HealthPageTestIdentifier.HEALTH_HISTORY_TABLE_ITEM, id)
            .assertIsDisplayed()
    }

    fun assertHealthHistoryTextIsDisplayed() {
        healthHistoryText.assertIsDisplayed()
    }

    fun assertHealthHistoryText(text: String) {
        healthHistoryText.assertIsDisplayed().assertTextEquals(text)
    }
}
