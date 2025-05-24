package edu.stanford.spezi.onboarding.composables

import androidx.compose.runtime.Composable
import edu.stanford.spezi.onboarding.LocalOnboardingNavigationPath
import edu.stanford.spezi.onboarding.OnboardingStack
import edu.stanford.spezi.onboarding.OnboardingTitle
import edu.stanford.spezi.onboarding.SequentialOnboarding
import edu.stanford.spezi.onboarding.SequentialOnboardingContent

@Composable
fun SequentialOnboardingTestComposable() {
    OnboardingStack {
        step("Welcome") {
            val path = LocalOnboardingNavigationPath.current
            SequentialOnboarding(
                title = "Things to know",
                subtitle = "And you should pay close attention ...",
                content = listOf(
                    SequentialOnboardingContent(
                        title = "A thing to know",
                        description = "This is a first thing that you should know, read carefully!"
                    ),
                    SequentialOnboardingContent(
                        title = "Second thing to know",
                        description = "This is a second thing that you should know, read carefully!"
                    ),
                    SequentialOnboardingContent(
                        title = "Third thing to know",
                        description = "This is a third thing that you should know, read carefully!"
                    ),
                    SequentialOnboardingContent(
                        description = "Now you should know all the things!"
                    ),
                ),
                actionText = "Continue",
                action = {
                    path.nextStep()
                }
            )
        }

        step("Done") {
            OnboardingTitle(
                "Done",
                "Sequential Onboarding done!"
            )
        }
    }
}
