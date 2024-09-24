package edu.stanford.spezi.modules.contact

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import org.junit.Rule
import org.junit.Test

class ContactViewTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun contactView_displaysContactName() {
        val contact = ContactFactory.mock

        composeTestRule.setContent {
            ContactView(contact)
        }

        composeTestRule.onNodeWithText(contact.name.formatted()).assertExists()
    }

    @Test
    fun contactView_displaysContactOptions() {
        val contact = ContactFactory.mock

        composeTestRule.setContent {
            ContactView(contact)
        }

        contact.options.forEach { option ->
            composeTestRule.onNodeWithText(option.title).assertExists()
        }
    }

    @Test
    fun contactView_displaysContactTitle() {
        val contact = ContactFactory.mock

        composeTestRule.setContent {
            ContactView(contact)
        }

        composeTestRule.onNodeWithText((contact.title ?: "") + (" - ") + (contact.organization ?: ""))
            .assertExists()
    }

    @Test
    fun contactView_displaysContactDescription() {
        val contact = ContactFactory.mock

        composeTestRule.setContent {
            ContactView(contact)
        }

        composeTestRule.onNodeWithText(contact.description ?: "").assertExists()
    }
}
