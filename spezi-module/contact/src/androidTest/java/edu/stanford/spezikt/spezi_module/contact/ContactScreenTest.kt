package edu.stanford.spezikt.spezi_module.contact

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import edu.stanford.spezikt.spezi_module.contact.model.ContactOption
import edu.stanford.spezikt.spezi_module.contact.model.ContactOptionType
import edu.stanford.spezikt.spezi_module.contact.repository.ContactRepository
import io.mockk.every
import io.mockk.mockk
import org.junit.Rule
import org.junit.Test
import java.util.UUID

class ContactScreenTest {

    private val mockContactRepository: ContactRepository = mockk()

    @get:Rule
    val composeTestRule = createAndroidComposeRule<TestActivity>()

    @Test
    fun contactView_displaysContactName() {
        val contact = ContactFactory.create(name = "John Doe")
        every { mockContactRepository.getContact() } returns contact

        composeTestRule.setContent {
            ContactScreen(ContactViewModel(mockContactRepository))
        }

        composeTestRule.onNodeWithText(contact.name).assertExists()
    }


    @Test
    fun contactView_displaysContactOptions() {
        val contact = ContactFactory.create(
            options = listOf(
                ContactOption(
                    UUID.randomUUID(),
                    "Call",
                    "+49 123 456 789",
                    Icons.Default.Call,
                    ContactOptionType.CALL
                ),
                ContactOption(
                    UUID.randomUUID(),
                    "Email",
                    "",
                    Icons.Default.Email,
                    ContactOptionType.EMAIL
                ),
                ContactOption(
                    UUID.randomUUID(),
                    "Website",
                    "https://www.google.com",
                    Icons.Default.Info,
                    ContactOptionType.WEBSITE
                )
            )
        )
        every { mockContactRepository.getContact() } returns contact

        composeTestRule.setContent {
            ContactScreen(
                ContactViewModel(mockContactRepository)
            )
        }

        mockContactRepository.getContact().options.forEach { option ->
            composeTestRule.onNodeWithText(option.name).assertExists()
        }
    }

    @Test
    fun contactView_displaysContactTitle() {
        val contact = ContactFactory.create(title = "CEO")
        every { mockContactRepository.getContact() } returns contact

        composeTestRule.setContent {
            ContactScreen(ContactViewModel(mockContactRepository))
        }

        composeTestRule.onNodeWithText(mockContactRepository.getContact().title)
            .assertExists()
    }

    @Test
    fun contactView_displaysContactDescription() {
        val contact =
            ContactFactory.create(description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.")
        every { mockContactRepository.getContact() } returns contact

        composeTestRule.setContent {
            ContactScreen(ContactViewModel(mockContactRepository))
        }

        composeTestRule.onNodeWithText(mockContactRepository.getContact().description)
            .assertExists()
    }
}