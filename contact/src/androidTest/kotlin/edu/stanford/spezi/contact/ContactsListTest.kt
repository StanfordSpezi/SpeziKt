package edu.stanford.spezi.contact

import androidx.compose.ui.test.junit4.createComposeRule
import edu.stanford.spezi.contact.simulator.ContactListSimulator
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ContactsListTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val contactsList = ContactsList(listOf(ContactFactory.leland, ContactFactory.mock))

    @Before
    fun init() {
        composeTestRule.setContent {
            contactsList.Content()
        }
    }

    @Test
    fun `test displays all contacts`() {
        contactsList {
            contactsList.contacts.forEach { contact ->
                assertHasContact(contact)
            }
        }
    }

    private fun contactsList(block: ContactListSimulator.() -> Unit) {
        ContactListSimulator(composeTestRule).apply(block)
    }
}
