package edu.stanford.spezi.ui.account

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import edu.stanford.spezi.ui.AsyncTextButton
import edu.stanford.spezi.ui.ChoicesFormFieldItem
import edu.stanford.spezi.ui.ComposableContent
import edu.stanford.spezi.ui.ImageResource
import edu.stanford.spezi.ui.StringResource
import edu.stanford.spezi.ui.theme.Sizes
import edu.stanford.spezi.ui.theme.Spacings
import edu.stanford.spezi.ui.theme.SpeziTheme
import edu.stanford.spezi.ui.theme.TextStyles
import edu.stanford.spezi.ui.theme.ThemePreviews
import edu.stanford.spezi.ui.theme.bold
import java.time.Instant

/**
 * Layout of the Sign up form
 *
 * @param headerIcon The icon to be displayed at the top of the form
 * @param title The title of the form
 * @param description The description of the form
 * @param sections The sections of the form
 * @param signUpButton The button to be displayed at the bottom of the form
 */
data class SignUpFormLayout(
    val headerIcon: ImageResource,
    val title: StringResource,
    val description: StringResource,
    val sections: List<SignUpSection>,
    val signUpButton: AsyncTextButton,
) : ComposableContent {
    @Composable
    override fun Content(modifier: Modifier) {
        Column(
            modifier = modifier
                .padding(Spacings.medium)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(Spacings.medium),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            headerIcon.Content(modifier = Modifier.size(Sizes.Icon.medium))
            Text(
                text = title.text(),
                style = TextStyles.headlineLarge.bold(),
            )

            Text(
                text = description.text(),
                textAlign = TextAlign.Center,
                style = TextStyles.bodyMedium,
            )

            sections.forEach { it.Content() }
            signUpButton.Content(modifier = Modifier.fillMaxWidth())
        }
    }
}

@ThemePreviews
@Composable
private fun Preview() {
    val options = listOf(1, 2)
    val layout = SignUpFormLayout(
        headerIcon = ImageResource(Icons.Default.AccountCircle),
        title = StringResource("Create a new Account"),
        description = StringResource("Please fill out the details below to create your new account."),
        sections = listOf(
            SignUpSection(
                title = StringResource("NAME"),
                entries = listOf(
                    SignUpFormEntry(
                        title = StringResource("First name"),
                        entry = StringDataEntry(placeholder = StringResource("Enter your first name")),
                        value = "John",
                        validationMessage = null,
                        onValueChange = {}
                    ),
                    SignUpFormEntry(
                        title = StringResource("Consent"),
                        entry = BooleanDataEntry(description = StringResource("Accept data collection")),
                        value = true,
                        onValueChange = {}
                    ),
                    SignUpFormEntry(
                        title = StringResource("Last name"),
                        entry = StringDataEntry(
                            placeholder = StringResource("Enter your last name"),
                            hideContent = false,
                        ),
                        value = "Smith",
                        onValueChange = {}
                    )
                )
            ),
            SignUpSection(
                title = StringResource("PERSONAL DETAILS"),
                entries = listOf(
                    SignUpFormEntry(
                        title = StringResource("Date of birth"),
                        entry = InstantDataEntry(
                            placeholder = StringResource("Select your birthday"),
                            formatter = { StringResource(it.toString()) },
                        ),
                        value = Instant.ofEpochMilli(0),
                        onValueChange = {}
                    ),
                    SignUpFormEntry(
                        title = StringResource("Choices"),
                        entry = ChoicesDataEntry(
                            choices = options,
                            optionTransformer = { ChoicesFormFieldItem.Option(id = it.toString(), label = StringResource(it.toString())) },
                            valueTransformer = { it },
                        ),
                        value = 1,
                        onValueChange = {}
                    ),

                )
            ),
            SignUpSection(
                title = StringResource("CREDENTIALS"),
                entries = listOf(
                    SignUpFormEntry(
                        title = StringResource("Email address"),
                        entry = StringDataEntry(placeholder = StringResource("Enter your email")),
                        value = "your.email@stanford.edu",
                        onValueChange = {}
                    ),
                    SignUpFormEntry(
                        title = StringResource("Password"),
                        entry = StringDataEntry(
                            placeholder = StringResource("Enter your password"),
                            hideContent = true,
                        ),
                        value = "12345778",
                        onValueChange = {}
                    ),
                )
            ),
        ),
        signUpButton = AsyncTextButton(
            title = StringResource("Signup"),
            action = {}
        ),
    )

    SpeziTheme {
        layout.Content()
    }
}
