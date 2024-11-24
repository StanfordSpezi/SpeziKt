package edu.stanford.spezi.module.onboarding.spezi.flow

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.navOptions
import edu.stanford.spezi.core.utils.UUID

class OnboardingNavigationPath internal constructor(
    internal val navController: NavController,
    internal var steps: List<OnboardingNavigationStep>,
) {
    internal val customSteps = mutableListOf<OnboardingNavigationStep>()

    private var currentOnboardingStep =
        navController.backQueue
            .firstOrNull { pathItem -> steps.any { pathItem.id == it.id } }?.id
            ?: steps.firstOrNull()?.id

    fun nextStep() {
        val currentOnboardingStepId = currentOnboardingStep
        val currentStepIndex = steps.indexOfFirst { it.id == currentOnboardingStepId }
        if (currentStepIndex < 0 || currentStepIndex + 1 >= steps.size) {
            return
        }

        navController.navigate(
            route = steps[currentStepIndex + 1].id,
            navOptions = navOptions {
                // TODO: Think about what to inject here
            },
            navigatorExtras = null, // TODO: Think about what to inject here
        )
    }

    fun append(id: String) {
        val step = steps.firstOrNull { it.id == id } ?: error("")

        navController.navigate(
            route = step.id,
            navOptions = navOptions {
                // TODO: Think about what to inject here
            },
            navigatorExtras = null, // TODO: Think about what to inject here
        )
    }

    fun append(content: @Composable () -> Unit) {
        val stepId = UUID().toString()
        customSteps.add(OnboardingNavigationStep(stepId, content))
        navController.navigate(
            route = stepId,
            navOptions = navOptions {
                // TODO: Think about what to inject here
            },
        )
    }

    fun removeLast() {
        navController.navigateUp()
    }
}
