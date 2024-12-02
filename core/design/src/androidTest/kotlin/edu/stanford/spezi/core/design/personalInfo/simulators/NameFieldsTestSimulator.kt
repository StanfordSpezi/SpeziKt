package edu.stanford.spezi.core.design.personalInfo.simulators

import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTextInput
import edu.stanford.spezi.core.design.views.personalinfo.PersonNameComponents
import edu.stanford.spezi.core.design.views.personalinfo.fields.NameTextFieldTestIdentifier
import edu.stanford.spezi.core.testing.onNodeWithIdentifier
import kotlin.reflect.KMutableProperty1

class NameFieldsTestSimulator(
    private val composeTestRule: ComposeTestRule,
) {
    fun assertTextExists(text: String) {
        composeTestRule
            .onNodeWithText(text)
            .assertExists()
    }

    fun enterText(property: KMutableProperty1<PersonNameComponents.Builder, String?>, text: String) {
        composeTestRule
            .onNodeWithIdentifier(NameTextFieldTestIdentifier.TEXT_FIELD, property.name)
            .performTextInput(text)
    }
}
