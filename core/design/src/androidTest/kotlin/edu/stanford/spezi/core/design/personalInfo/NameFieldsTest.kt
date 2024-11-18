package edu.stanford.spezi.core.design.personalInfo

import android.app.Person
import androidx.compose.ui.test.junit4.createComposeRule
import edu.stanford.spezi.core.design.personalInfo.composables.NameFieldsTestComposable
import edu.stanford.spezi.core.design.personalInfo.simulators.NameFieldsTestSimulator
import edu.stanford.spezi.core.design.views.personalinfo.PersonNameComponents
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

            enterText(PersonNameComponents.Builder::givenName, "Leland")
            enterText(PersonNameComponents.Builder::familyName, "Stanford")
        }
    }

    private fun nameFields(block: NameFieldsTestSimulator.() -> Unit) {
        NameFieldsTestSimulator(composeTestRule).apply(block)
    }
}
