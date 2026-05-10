package edu.stanford.spezi.ui.account

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import edu.stanford.spezi.testing.screenshot.ScreenshotTest
import edu.stanford.spezi.ui.ChoicesFormFieldItem
import edu.stanford.spezi.ui.StringResource
import edu.stanford.spezi.ui.theme.Spacings
import org.junit.Test
import java.time.Instant

class SignUpFormEntryScreenshotTest : ScreenshotTest() {

    @Test
    fun `StringDataEntry empty screenshot`() {
        val entry = SignUpFormEntry(
            title = StringResource("Last name"),
            entry = StringDataEntry(
                placeholder = StringResource("Enter your last name"),
                hideContent = false,
            ),
            value = "",
            onValueChange = {},
            validationMessage = null,
        )

        entryScreenshot(entry)
    }

    @Test
    fun `StringDataEntry filled screenshot`() {
        val entry = SignUpFormEntry(
            title = StringResource("Last name"),
            entry = StringDataEntry(
                placeholder = StringResource("Enter your last name"),
                hideContent = false,
            ),
            value = "Stanford",
            onValueChange = {},
            validationMessage = null,
        )

        entryScreenshot(entry)
    }

    @Test
    fun `StringDataEntry invalid input screenshot`() {
        val entry = SignUpFormEntry(
            title = StringResource("Phone number"),
            entry = StringDataEntry(
                placeholder = StringResource("Enter your phone number"),
                hideContent = false,
            ),
            value = "0123456789",
            onValueChange = {},
            validationMessage = StringResource("Your phone number is invalid"),
        )

        entryScreenshot(entry)
    }

    @Test
    fun `StringDataEntry password input screenshot`() {
        val entry = SignUpFormEntry(
            title = StringResource("Password"),
            entry = StringDataEntry(
                placeholder = StringResource("Enter your password"),
                hideContent = true,
            ),
            value = "top-secret",
            onValueChange = {},
        )

        entryScreenshot(entry)
    }

    @Test
    fun `BooleanData entry screenshot`() {
        val entry = SignUpFormEntry(
            title = StringResource("Consent"),
            entry = BooleanDataEntry(
                description = StringResource("Accept data collection"),
            ),
            value = true,
            onValueChange = {},
        )

        entryScreenshot(entry)
    }

    @Test
    fun `InstantData entry empty screenshot`() {
        val entry = SignUpFormEntry(
            title = StringResource("Date of Birth"),
            entry = InstantDataEntry(
                placeholder = StringResource("Select your birth date"),
                formatter = { "" },
            ),
            value = Instant.now(),
            onValueChange = {},
        )

        entryScreenshot(entry)
    }

    @Test
    fun `InstantData entry value screenshot`() {
        val entry = SignUpFormEntry(
            title = StringResource("Date of Birth"),
            entry = InstantDataEntry(
                placeholder = StringResource("Select your birth date"),
                formatter = { "01.01.2026" },
            ),
            value = Instant.now(),
            onValueChange = {},
        )

        entryScreenshot(entry)
    }

    @Test
    fun `ChoicesDataEntry screenshot`() {
        val genders = listOf("Male", "Female", "Other")
        val entry = SignUpFormEntry(
            title = StringResource("Gender identity"),
            entry = ChoicesDataEntry(
                choices = genders,
                optionTransformer = { ChoicesFormFieldItem.Option(id = it, label = StringResource(it)) },
                valueTransformer = { it },
            ),
            value = "Other",
            onValueChange = {},
        )

        entryScreenshot(entry)
    }

    private fun entryScreenshot(entry: AnySignUpFormEntry) {
        screenshot {
            Box(modifier = Modifier.padding(Spacings.small)) {
                entry.Content()
            }
        }
    }
}
