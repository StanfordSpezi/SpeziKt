package edu.stanford.spezi.questionnaire.simulators

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.ComposeTestRule
import edu.stanford.spezi.questionnaire.QuestionnaireComposableTestIdentifiers
import edu.stanford.spezi.ui.testing.onNodeWithIdentifier

class QuestionnaireTestSimulator(
    val composeTestRule: ComposeTestRule,
) {
    private val root =
        composeTestRule.onNodeWithIdentifier(QuestionnaireComposableTestIdentifiers.ROOT)

    fun assertIsDisplayed() {
        root.assertIsDisplayed()
    }
}
