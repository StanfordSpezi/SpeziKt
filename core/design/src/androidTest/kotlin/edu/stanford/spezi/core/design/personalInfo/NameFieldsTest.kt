package edu.stanford.spezi.core.design.personalInfo

import androidx.compose.ui.test.junit4.createComposeRule
import com.google.common.truth.Truth.assertThat
import edu.stanford.spezi.core.design.personalInfo.composables.NameFieldsTestComposable
import edu.stanford.spezi.core.design.personalInfo.simulators.NameFieldsTestSimulator
import edu.stanford.spezi.core.design.views.personalinfo.PersonNameComponents
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class NameFieldsTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    val nameBuilder = PersonNameComponents.Builder()

    @Before
    fun init() {
        composeTestRule.setContent {
            NameFieldsTestComposable(nameBuilder)
        }
    }

    @Test
    fun testNameFields() {
        val givenName = "Leland"
        val familyName = "Stanford"

        nameFields {
            assertTextExists("First Name")
            assertTextExists("Last Name")

            enterText(PersonNameComponents.Builder::givenName, givenName)
            enterText(PersonNameComponents.Builder::familyName, familyName)

            assertTextExists("First Name")
            assertTextExists("Last Name")

            assertThat(nameBuilder.givenName).isEqualTo(givenName)
            assertThat(nameBuilder.familyName).isEqualTo(familyName)
        }
    }

    private fun nameFields(block: NameFieldsTestSimulator.() -> Unit) {
        NameFieldsTestSimulator(composeTestRule).apply(block)
    }
}
