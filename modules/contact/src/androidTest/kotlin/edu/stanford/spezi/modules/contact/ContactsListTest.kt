package edu.stanford.spezi.modules.contact

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import org.junit.Rule
import org.junit.Test

class ContactsListTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun contactsList_check() {
        val contacts = listOf(ContactFactory.leland, ContactFactory.mock)

        composeTestRule.setContent {
            ContactsList(contacts)
        }

        composeTestRule.onNodeWithText(contacts[0].name.formatted()).assertExists()
    }
}
