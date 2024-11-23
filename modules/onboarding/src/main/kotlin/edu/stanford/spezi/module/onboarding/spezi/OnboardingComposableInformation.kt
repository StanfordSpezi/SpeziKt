package edu.stanford.spezi.module.onboarding.spezi

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Email
import androidx.compose.runtime.Composable
import edu.stanford.spezi.core.design.component.ImageResource
import edu.stanford.spezi.core.design.component.StringResource
import edu.stanford.spezi.core.design.component.StringResource.Companion.invoke
import edu.stanford.spezi.core.design.theme.SpeziTheme
import edu.stanford.spezi.core.design.theme.ThemePreviews
import edu.stanford.spezi.module.onboarding.core.OnboardingComposable
import edu.stanford.spezi.module.onboarding.core.OnboardingTitle

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

    SpeziTheme(isPreview = true) {
        OnboardingComposable(
            "Title",
            "Subtitle",
            areas,
            actionText = "Action",
            action = {}
        )
    }
}
