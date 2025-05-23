package edu.stanford.spezi.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import edu.stanford.spezi.ui.ImageResource
import edu.stanford.spezi.ui.StringResource
import edu.stanford.spezi.ui.theme.Colors
import edu.stanford.spezi.ui.theme.Spacings
import edu.stanford.spezi.ui.theme.SpeziTheme
import edu.stanford.spezi.ui.theme.TextStyles
import edu.stanford.spezi.ui.theme.ThemePreviews

data class OnboardingInformationContent(
    val icon: @Composable () -> Unit,
    val title: String,
    val description: String,
) {
    constructor(
        icon: ImageResource,
        title: String,
        description: String,
    ) : this(
        icon = { icon.Content() },
        title = title,
        description = description,
    )
}

@Composable
fun OnboardingInformation(areas: List<OnboardingInformationContent>) {
    Column(
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(30.dp)
    ) {
        for (area in areas) {
            OnboardingInformationArea(area)
        }
    }
}

@Composable
private fun OnboardingInformationArea(content: OnboardingInformationContent) {
    Row(
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Box(
            modifier = Modifier
                .padding(Spacings.small)
                .padding(end = 10.dp)
                .width(40.dp),
        ) {
            content.icon()
        }

        Column {
            Text(
                content.title,
                style = TextStyles.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                ),
            )

            Text(
                content.description,
                style = TextStyles.bodyMedium.copy(
                    color = Colors.secondary,
                ),
            )
        }
    }
}

@ThemePreviews
@Composable
private fun OnboardingInformationPreview() {
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
        OnboardingInformation(areas)
    }
}
