package edu.stanford.spezi.spezi.questionnaire.simulators

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.ComposeTestRule
import edu.stanford.spezi.core.testing.onNodeWithIdentifier
import edu.stanford.spezi.spezi.questionnaire.QuestionnaireComposableTestIdentifiers

class QuestionnaireTestSimulator(
    val composeTestRule: ComposeTestRule,
) {
    private val root =
        composeTestRule.onNodeWithIdentifier(QuestionnaireComposableTestIdentifiers.ROOT)

    fun assertIsDisplayed() {
        root.assertIsDisplayed()
    }
}
