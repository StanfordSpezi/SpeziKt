package edu.stanford.spezi.contact.simulator

import androidx.compose.ui.test.junit4.ComposeTestRule
import edu.stanford.spezi.contact.Contact
import edu.stanford.spezi.contact.ContactsListTestIdentifier
import edu.stanford.spezi.ui.testing.onNodeWithIdentifier

class ContactListSimulator(
    private val composeTestRule: ComposeTestRule,
) {
    private fun contactContent(contact: Contact) =
        composeTestRule.onNodeWithIdentifier(ContactsListTestIdentifier.CONTACT, contact.id.toString())

    fun assertHasContact(contact: Contact) {
        contactContent(contact).assertExists()
    }
}
