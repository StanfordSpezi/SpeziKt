package edu.stanford.spezi.ui.account

import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import edu.stanford.spezi.testing.screenshot.ScreenshotTest
import edu.stanford.spezi.ui.StringResource
import edu.stanford.spezi.ui.theme.Spacings
import org.junit.Test

class SignUpSectionScreenshotTest : ScreenshotTest() {

    @Test
    fun `SignUpSection screenshot`() {
        val section = SignUpSection(
            title = StringResource("PERSONAL DETAILS"),
            entries = listOf(
                SignUpFormEntry(
                    title = StringResource("First name"),
                    entry = StringDataEntry(placeholder = StringResource("Enter your first name")),
                    value = "John",
                    onValueChange = {}
                ),
                SignUpFormEntry(
                    title = StringResource("Last name"),
                    entry = StringDataEntry(
                        placeholder = StringResource("Enter your last name"),
                        hideContent = false,
                    ),
                    value = "",
                    onValueChange = {}
                ),
                SignUpFormEntry(
                    title = StringResource("Data collection"),
                    entry = BooleanDataEntry(description = StringResource("Allow data collection")),
                    value = true,
                    onValueChange = {}
                ),
            )
        )

        screenshot {
            section.Content(modifier = Modifier.padding(Spacings.small))
        }
    }
}
