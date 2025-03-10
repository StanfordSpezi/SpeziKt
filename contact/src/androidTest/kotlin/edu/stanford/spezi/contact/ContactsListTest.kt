package edu.stanford.spezi.contact

import androidx.compose.ui.test.junit4.createComposeRule
import edu.stanford.spezi.contact.simulator.ContactListSimulator
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ContactsListTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val contacts = listOf(ContactFactory.leland, ContactFactory.mock)

    @Before
    fun init() {
        composeTestRule.setContent {
            ContactsList(contacts)
        }
    }

    @Test
    fun `test displays all contacts`() {
        contactsList {
            for (contact in contacts) {
                assertHasContact(contact)
            }
        }
    }

    private fun contactsList(block: ContactListSimulator.() -> Unit) {
        ContactListSimulator(composeTestRule).apply(block)
    }
}
