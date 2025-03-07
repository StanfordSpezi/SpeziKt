package edu.stanford.spezi.spezi.questionnaire.simulators

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.ComposeTestRule
import edu.stanford.spezi.spezi.questionnaire.QuestionnaireComposableTestIdentifiers
import edu.stanford.spezi.spezi.ui.helpers.testing.onNodeWithIdentifier

class QuestionnaireTestSimulator(
    val composeTestRule: ComposeTestRule,
) {
    private val root =
        composeTestRule.onNodeWithIdentifier(QuestionnaireComposableTestIdentifiers.ROOT)

    fun assertIsDisplayed() {
        root.assertIsDisplayed()
    }
}
