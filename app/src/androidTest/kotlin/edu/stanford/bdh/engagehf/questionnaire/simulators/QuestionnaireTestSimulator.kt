package edu.stanford.bdh.engagehf.questionnaire.simulators

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.ComposeTestRule
import edu.stanford.bdh.engagehf.questionnaire.spezi.QuestionnaireComposableTestIdentifiers
import edu.stanford.spezi.core.testing.onNodeWithIdentifier

class QuestionnaireTestSimulator(
    composeTestRule: ComposeTestRule,
) {
    private val root =
        composeTestRule.onNodeWithIdentifier(QuestionnaireComposableTestIdentifiers.ROOT)

    fun assertIsDisplayed() {
        root.assertIsDisplayed()
    }
}
