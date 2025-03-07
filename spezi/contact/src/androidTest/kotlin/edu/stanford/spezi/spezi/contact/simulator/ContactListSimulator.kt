package edu.stanford.spezi.spezi.contact.simulator

import androidx.compose.ui.test.junit4.ComposeTestRule
import edu.stanford.spezi.spezi.contact.ContactsListTestIdentifier
import edu.stanford.spezi.spezi.contact.model.Contact
import edu.stanford.spezi.spezi.ui.helpers.testing.onNodeWithIdentifier

class ContactListSimulator(
    private val composeTestRule: ComposeTestRule,
) {
    private fun contactComposable(contact: Contact) =
        composeTestRule.onNodeWithIdentifier(ContactsListTestIdentifier.CONTACT, contact.id.toString())

    fun assertHasContact(contact: Contact) {
        contactComposable(contact).assertExists()
    }
}
