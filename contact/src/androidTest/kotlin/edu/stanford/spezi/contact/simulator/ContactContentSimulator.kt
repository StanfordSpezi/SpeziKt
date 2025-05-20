package edu.stanford.spezi.contact.simulator

import android.location.Address
import androidx.compose.ui.test.assertContentDescriptionContains
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onChildAt
import androidx.test.platform.app.InstrumentationRegistry
import edu.stanford.spezi.contact.ContactContentTestIdentifier
import edu.stanford.spezi.contact.ContactOption
import edu.stanford.spezi.contact.formatted
import edu.stanford.spezi.testing.ui.onNodeWithContent
import edu.stanford.spezi.testing.ui.onNodeWithIdentifier
import edu.stanford.spezi.ui.LocalImageResource
import edu.stanford.spezi.ui.StringResource
import edu.stanford.spezi.ui.personalinfo.PersonNameComponents

class ContactContentSimulator(
    private val composeTestRule: ComposeTestRule,
) {
    private fun image(image: LocalImageResource) =
        composeTestRule.onNodeWithContent(image.identifier)

    private val name =
        composeTestRule.onNodeWithIdentifier(ContactContentTestIdentifier.NAME)

    private val subtitle =
        composeTestRule.onNodeWithIdentifier(ContactContentTestIdentifier.SUBTITLE)

    private val description =
        composeTestRule.onNodeWithIdentifier(ContactContentTestIdentifier.DESCRIPTION)

    private val address =
        composeTestRule.onNodeWithIdentifier(ContactContentTestIdentifier.ADDRESS)

    private val targetContext =
        InstrumentationRegistry.getInstrumentation().targetContext

    private fun option(title: StringResource) =
        composeTestRule.onNodeWithIdentifier(
            ContactContentTestIdentifier.OPTION,
            title.get(targetContext)
        )

    fun assertHasImage(imageResource: LocalImageResource?) {
        imageResource?.let {
            image(imageResource)
                .assertExists()
                .assertContentDescriptionContains("Account Box")
        }
    }

    fun assertHasName(text: PersonNameComponents?) {
        text?.let {
            name.assertExists()
                .assertTextEquals(it.formatted())
        } ?: name.assertDoesNotExist()
    }

    fun assertHasSubtitleContaining(text: StringResource?) {
        text?.let {
            subtitle
                .assertExists()
                .assertTextContains(it.get(targetContext), substring = true)
        }
    }

    fun assertHasDescription(text: StringResource?) {
        text?.let {
            description
                .assertExists()
                .assertTextEquals(it.get(targetContext))
        } ?: description.assertDoesNotExist()
    }

    fun assertHasOption(option: ContactOption) {
        option(option.title)
            .assertExists()
    }

    fun assertHasAddress(value: Address?) {
        value?.let {
            address
                .assertExists()
                .onChildAt(0)
                .assertTextEquals(it.formatted())
        } ?: address.assertDoesNotExist()
    }
}
