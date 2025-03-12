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
        contactComposable {
            assertHasImage(contact.image)
        }
    }

    @Test
    fun `test displays contact name`() {
        contactComposable {
            assertHasName(contact.name)
        }
    }

    @Test
    fun `test displays contact options`() {
        contactComposable {
            for (option in contact.options) {
                assertHasOption(option)
            }
        }
    }

    @Test
    fun `test displays contact title`() {
        contactComposable {
            assertHasSubtitleContaining(contact.title)
        }
    }

    @Test
    fun `test displays contact organization`() {
        contactComposable {
            assertHasSubtitleContaining(contact.organization)
        }
    }

    @Test
    fun `test displays contact description`() {
        contactComposable {
            assertHasDescription(contact.description)
        }
    }

    @Test
    fun `test displays contact address`() {
        contactComposable {
            assertHasAddress(contact.address)
        }
    }

    private fun contactComposable(block: ContactContentSimulator.() -> Unit) {
        ContactContentSimulator(composeTestRule).apply(block)
    }
}
