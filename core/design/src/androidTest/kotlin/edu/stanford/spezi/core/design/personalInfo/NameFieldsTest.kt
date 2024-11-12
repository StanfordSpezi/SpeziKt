package edu.stanford.spezi.core.design.personalInfo

import androidx.compose.ui.test.junit4.createComposeRule
import edu.stanford.spezi.core.design.personalInfo.composables.NameFieldsTestComposable
import edu.stanford.spezi.core.design.personalInfo.simulators.NameFieldsTestSimulator
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class NameFieldsTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Before
    fun init() {
        composeTestRule.setContent {
            NameFieldsTestComposable()
        }
    }

    @Test
    fun testNameFields() {
        nameFields {
            assertTextExists("First Name")
            assertTextExists("Last Name")

            enterText("enter your first name", "Leland")
            enterText("enter your last name", "Stanford")
        }
    }

    private fun nameFields(block: NameFieldsTestSimulator.() -> Unit) {
        NameFieldsTestSimulator(composeTestRule).apply(block)
    }
}
