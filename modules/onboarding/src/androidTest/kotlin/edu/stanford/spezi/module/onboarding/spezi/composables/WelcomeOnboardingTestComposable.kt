package edu.stanford.spezi.module.onboarding.spezi.composables

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.runtime.Composable
import edu.stanford.spezi.core.design.component.ImageResource
import edu.stanford.spezi.core.design.component.StringResource
import edu.stanford.spezi.module.onboarding.core.OnboardingTitle
import edu.stanford.spezi.module.onboarding.spezi.OnboardingComposable
import edu.stanford.spezi.module.onboarding.spezi.OnboardingInformationContent
import edu.stanford.spezi.module.onboarding.spezi.flow.LocalOnboardingNavigationPath
import edu.stanford.spezi.module.onboarding.spezi.flow.OnboardingStack

@Composable
fun WelcomeOnboardingTestComposable() {
    OnboardingStack {
        step("Welcome") {
            val path = LocalOnboardingNavigationPath.current
            OnboardingComposable(
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
