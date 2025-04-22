package edu.stanford.spezi.contact.simulator

import androidx.compose.ui.test.junit4.ComposeTestRule
import edu.stanford.spezi.contact.Contact
import edu.stanford.spezi.testing.ui.onNodeWithContent

class ContactListSimulator(
    private val composeTestRule: ComposeTestRule,
) {
    fun assertHasContact(contact: Contact) {
        composeTestRule.onNodeWithContent(contact.id.toString()).assertExists()
    }
}
