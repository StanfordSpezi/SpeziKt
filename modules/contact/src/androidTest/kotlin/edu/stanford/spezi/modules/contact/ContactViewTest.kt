package edu.stanford.spezi.modules.contact

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import edu.stanford.spezi.modules.contact.model.ContactOption
import org.junit.Rule
import org.junit.Test

class ContactViewTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun contactView_displaysContactName() {
        val contact = ContactFactory.create(name = "John Doe")

        composeTestRule.setContent {
            ContactView(contact)
        }

        composeTestRule.onNodeWithText(contact.name).assertExists()
    }

    @Test
    fun contactView_displaysContactOptions() {
        val contact = ContactFactory.create(
            options = listOf(
                ContactOption.call("+49 123 456 789"),
                ContactOption.email(listOf()),
                ContactOption.website("https://www.google.com")
            )
        )

        composeTestRule.setContent {
            ContactView(contact)
        }

        contact.options.forEach { option ->
            composeTestRule.onNodeWithText(option.title).assertExists()
        }
    }

    @Test
    fun contactView_displaysContactTitle() {
        val contact = ContactFactory.create(title = "CEO")

        composeTestRule.setContent {
            ContactView(contact)
        }

        composeTestRule.onNodeWithText(contact.title ?: "")
            .assertExists()
    }

    @Test
    fun contactView_displaysContactDescription() {
        val description = "Lorem ipsum dolor sit amet"
        val contact = ContactFactory.create(description = description)

        composeTestRule.setContent {
            ContactView(contact)
        }

        composeTestRule.onNodeWithText(description).assertExists()
    }
}
