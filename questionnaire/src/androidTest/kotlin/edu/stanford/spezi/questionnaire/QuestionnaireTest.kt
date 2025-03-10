package edu.stanford.spezi.questionnaire

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import edu.stanford.spezi.questionnaire.composables.QuestionnaireTestComposable
import edu.stanford.spezi.questionnaire.simulators.QuestionnaireTestSimulator
import edu.stanford.spezi.ui.testing.ComposeContentActivity
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class QuestionnaireTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

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
