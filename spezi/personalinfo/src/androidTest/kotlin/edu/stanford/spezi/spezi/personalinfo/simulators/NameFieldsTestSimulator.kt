package edu.stanford.spezi.spezi.personalinfo.simulators

import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTextInput
import edu.stanford.spezi.core.testing.onNodeWithIdentifier
import edu.stanford.spezi.spezi.personalinfo.PersonNameComponents
import edu.stanford.spezi.spezi.personalinfo.fields.NameTextFieldTestIdentifier
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
