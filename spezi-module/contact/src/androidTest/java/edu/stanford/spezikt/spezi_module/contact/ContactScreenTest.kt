package edu.stanford.spezikt.spezi_module.contact

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import edu.stanford.spezikt.spezi_module.contact.model.Contact
import edu.stanford.spezikt.spezi_module.contact.model.ContactOption
import edu.stanford.spezikt.spezi_module.contact.model.ContactOptionType
import edu.stanford.spezikt.spezi_module.contact.repository.ContactRepository
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.whenever
import java.util.UUID

class ContactScreenTest {

    private val mockContactRepository: ContactRepository = mock()

    @get:Rule
    val composeTestRule = createComposeRule()


    @Before
    fun setup() {
        whenever(mockContactRepository.getContact()).thenReturn(createContact())
    }

    @Test
    fun contactView_displaysContactName() {

        composeTestRule.setContent {
            ContactScreen(mockContactRepository.getContact())
        }

        composeTestRule.onNodeWithText(mockContactRepository.getContact().name).assertExists()
    }


    @Test
    fun contactView_displaysContactOptions() {

        composeTestRule.setContent {
            ContactScreen(mockContactRepository.getContact())
        }

        mockContactRepository.getContact().options.forEach { option ->
            composeTestRule.onNodeWithText(option.name).assertExists()
        }
    }

    @Test
    fun contactView_displaysContactTitle() {
        composeTestRule.setContent {
            ContactScreen(mockContactRepository.getContact())
        }

        composeTestRule.onNodeWithText(mockContactRepository.getContact().title).assertExists()
    }

    @Test
    fun contactView_displaysContactDescription() {
        composeTestRule.setContent {
            ContactScreen(mockContactRepository.getContact())
        }

        composeTestRule.onNodeWithText(mockContactRepository.getContact().description)
            .assertExists()
    }

    private fun createContact(): Contact {
        return Contact(
            UUID.randomUUID(),
            null,
            "John Doe",
            "CEO",
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.",
            "Company Inc.",
            "1234 Main Street, 12345 City",
            listOf(
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
    }
}