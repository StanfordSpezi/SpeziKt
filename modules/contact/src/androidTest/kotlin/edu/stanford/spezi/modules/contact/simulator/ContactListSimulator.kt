package edu.stanford.spezi.modules.contact.simulator

import androidx.compose.ui.test.junit4.ComposeTestRule
import edu.stanford.spezi.core.testing.onNodeWithIdentifier
import edu.stanford.spezi.modules.contact.ContactsListTestIdentifier
import edu.stanford.spezi.modules.contact.model.Contact

class ContactListSimulator(
    private val composeTestRule: ComposeTestRule,
) {
    private fun contactComposable(contact: Contact) =
        composeTestRule.onNodeWithIdentifier(ContactsListTestIdentifier.CONTACT, contact.id.toString())

    fun assertHasContact(contact: Contact) {
        contactComposable(contact).assertExists()
    }
}
