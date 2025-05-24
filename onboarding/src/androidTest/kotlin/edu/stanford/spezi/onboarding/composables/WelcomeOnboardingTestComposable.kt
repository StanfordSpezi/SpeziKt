package edu.stanford.spezi.onboarding.composables

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.runtime.Composable
import edu.stanford.spezi.onboarding.LocalOnboardingNavigationPath
import edu.stanford.spezi.onboarding.Onboarding
import edu.stanford.spezi.onboarding.OnboardingInformationContent
import edu.stanford.spezi.onboarding.OnboardingStack
import edu.stanford.spezi.onboarding.OnboardingTitle
import edu.stanford.spezi.ui.ImageResource
import edu.stanford.spezi.ui.StringResource

@Composable
fun WelcomeOnboardingTestComposable() {
    OnboardingStack {
        step("Welcome") {
            val path = LocalOnboardingNavigationPath.current
            Onboarding(
                title = "Welcome",
                subtitle = "Spezi UI Tests",
                areas = listOf(
                    OnboardingInformationContent(
                        icon = ImageResource.Vector(Icons.Default.Done, StringResource("Icon")),
                        title = "Tortoise",
                        description = "A Tortoise!"
                    ),
                    OnboardingInformationContent(
                        icon = ImageResource.Vector(Icons.Default.Done, StringResource("Icon")),
                        title = "Tree",
                        description = "A Tree!"
                    ),
                    OnboardingInformationContent(
                        icon = ImageResource.Vector(Icons.Default.Done, StringResource("Icon")),
                        title = "Letter",
                        description = "A letter!"
                    ),
                    OnboardingInformationContent(
                        icon = ImageResource.Vector(Icons.Default.Done, StringResource("Icon")),
                        title = "Circle",
                        description = "A circle!"
                    ),
                ),
                actionText = "Learn More",
                action = {
                    path.nextStep()
                }
            )
        }

        step("Done") {
            OnboardingTitle(
                "Done",
                "Welcome Onboarding done!"
            )
        }
    }
}
