package edu.stanford.spezi.onboarding

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Email
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import edu.stanford.spezi.ui.ImageResource
import edu.stanford.spezi.ui.StringResource
import edu.stanford.spezi.ui.StringResource.Companion.invoke
import edu.stanford.spezi.ui.theme.SpeziTheme
import edu.stanford.spezi.ui.theme.ThemePreviews

@Composable
fun Onboarding(
    title: String,
    subtitle: String? = null,
    areas: List<OnboardingInformationContent>,
    actionText: String,
    action: suspend () -> Unit,
) {
    Onboarding(
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

@Composable
fun Onboarding(
    modifier: Modifier = Modifier,
    title: @Composable () -> Unit = {},
    content: @Composable () -> Unit,
    action: (@Composable () -> Unit)? = null,
) {
    val size = remember { mutableStateOf(IntSize.Zero) }
    Box(modifier.onSizeChanged { size.value = it }) {
        LazyColumn(Modifier.padding(24.dp)) {
            item {
                Column(Modifier.heightIn(min = size.value.height.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                        title()
                        content()
                    }
                    action?.let { action ->
                        Spacer(Modifier.fillMaxHeight())
                        action()
                    }
                    Spacer(Modifier.height(10.dp))
                }
            }
        }
    }
}

@ThemePreviews
@Composable
private fun OnboardingPreview() {
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
        Onboarding(
            title = "Title",
            subtitle = "Subtitle",
            areas = areas,
            actionText = "Action",
            action = {}
        )
    }
}
