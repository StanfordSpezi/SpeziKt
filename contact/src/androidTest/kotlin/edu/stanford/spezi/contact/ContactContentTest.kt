package edu.stanford.spezi.contact

import androidx.compose.ui.test.junit4.createComposeRule
import edu.stanford.spezi.contact.simulator.ContactContentSimulator
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ContactContentTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val contact = ContactFactory.mock

    @Before
    fun init() {
        composeTestRule.setContent {
            contact.Content()
        }
    }

    @Test
    fun `test displays contact image`() {
        contactContent {
            assertHasImage(contact.image)
        }
    }

    @Test
    fun `test displays contact name`() {
        contactContent {
            assertHasName(contact.name)
        }
    }

    @Test
    fun `test displays contact options`() {
        contactContent {
            for (option in contact.options) {
                assertHasOption(option)
            }
        }
    }

    @Test
    fun `test displays contact title`() {
        contactContent {
            assertHasSubtitleContaining(contact.title)
        }
    }

    @Test
    fun `test displays contact organization`() {
        contactContent {
            assertHasSubtitleContaining(contact.organization)
        }
    }

    @Test
    fun `test displays contact description`() {
        contactContent {
            assertHasDescription(contact.description)
        }
    }

    @Test
    fun `test displays contact address`() {
        contactContent {
            assertHasAddress(contact.address)
        }
    }

    private fun contactContent(block: ContactContentSimulator.() -> Unit) {
        ContactContentSimulator(composeTestRule).apply(block)
    }
}
