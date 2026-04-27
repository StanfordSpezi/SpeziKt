package edu.stanford.spezi.questionnaire

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import edu.stanford.spezi.questionnaire.composables.QuestionnaireTestComposable
import edu.stanford.spezi.questionnaire.simulators.QuestionnaireTestSimulator
import edu.stanford.spezi.testing.ui.ComposeContentActivity
import org.junit.Rule
import org.junit.Test

class QuestionnaireTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComposeContentActivity>()

    @Test
    fun testQuestionnaireComposableDisplay() {
        composeTestRule.activity.setScreen { QuestionnaireTestComposable() }
        questionnaireComposable {
            assertIsDisplayed()
        }
    }

    private fun questionnaireComposable(block: QuestionnaireTestSimulator.() -> Unit) {
        QuestionnaireTestSimulator(composeTestRule).apply { block() }
    }
}
