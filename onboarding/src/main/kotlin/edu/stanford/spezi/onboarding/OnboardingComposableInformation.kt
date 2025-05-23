package edu.stanford.spezi.onboarding

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Email
import androidx.compose.runtime.Composable
import edu.stanford.spezi.ui.ImageResource
import edu.stanford.spezi.ui.StringResource
import edu.stanford.spezi.ui.theme.SpeziTheme
import edu.stanford.spezi.ui.theme.ThemePreviews

@Composable
fun OnboardingComposable(
    title: String,
    subtitle: String? = null,
    areas: List<OnboardingInformationContent>,
    actionText: String,
    action: suspend () -> Unit,
) {
    OnboardingComposable(
        title = {
            OnboardingTitle(title, subtitle)
        },
        content = {
            OnboardingInformation(areas)
        },
        action = {
            OnboardingActions(actionText, action)
        }
    )
}

@ThemePreviews
@Composable
private fun OnboardingComposablePreview() {
    val areas = listOf(
        OnboardingInformationContent(
            icon = ImageResource.Vector(Icons.Default.Email, StringResource("Email")),
            title = "Email",
            description = "This is an email. And we can write a lot about E-Mails in a section like this. A very long text!"
        ),
        OnboardingInformationContent(
            icon = ImageResource.Vector(Icons.Default.Build, StringResource("Wrench")),
            title = "Wrench",
            description = "This is a wrench!"
        ),
        OnboardingInformationContent(
            icon = ImageResource.Vector(Icons.Default.Call, StringResource("Phone")),
            title = "Phone",
            description = "This is a phone."
        )
    )

    SpeziTheme {
        OnboardingComposable(
            "Title",
            "Subtitle",
            areas,
            actionText = "Action",
            action = {}
        )
    }
}
