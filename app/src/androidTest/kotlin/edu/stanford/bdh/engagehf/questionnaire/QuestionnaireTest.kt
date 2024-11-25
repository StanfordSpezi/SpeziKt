package edu.stanford.bdh.engagehf.questionnaire

import androidx.compose.ui.test.junit4.createComposeRule
import edu.stanford.bdh.engagehf.questionnaire.composables.QuestionnaireTestComposable
import edu.stanford.bdh.engagehf.questionnaire.simulators.QuestionnaireTestSimulator
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class QuestionnaireTest {

    @get: Rule
    private val composeRule = createComposeRule()

    @Before
    fun init() {
        try {
            composeRule.setContent {
                QuestionnaireTestComposable()
            }
        } catch (error: Throwable) {
            // TODO: I currently do not know how to fix this
            //  and how to set the view up to work in a test environment
            //  - help much appreciated!
            println("Error occurred: $error")
        }
    }

    @Test
    fun testQuestionnaireComposable() {
        // TODO: This should actually contain logic, but is skipped for now
        questionnaireComposable {
            println("Empty test")
        }
    }

    private fun questionnaireComposable(block: QuestionnaireTestSimulator.() -> Unit) {
        QuestionnaireTestSimulator(composeRule).apply { block() }
    }
}
