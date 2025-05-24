package edu.stanford.spezi.onboarding

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.navigation.NavController
import androidx.navigation.NavGraph
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.createGraph
import androidx.navigation.navArgument
import androidx.navigation.navOptions
import edu.stanford.spezi.foundation.UUID

class OnboardingNavigationPath internal constructor(
    internal val navController: NavController,
    internal var startDestination: String,
    internal var steps: List<OnboardingStep>,
) {
    private val customSteps = mutableListOf<OnboardingStep>()

    private val currentOnboardingStepId: String? get() {
        val currentQueue = navController.backQueue
        val lastStep = currentQueue.lastOrNull { queueItem ->
            !(queueItem.destination.route ?: "").startsWith(customRoute(""))
        }

        return lastStep?.destination?.route ?: steps.firstOrNull()?.id
    }

    fun nextStep() {
        val currentOnboardingStepId = currentOnboardingStepId
        val currentStepIndex = steps.indexOfFirst { it.id == currentOnboardingStepId }
        if (currentStepIndex < 0 || currentStepIndex + 1 >= steps.size) {
            return
        }

        navController.navigate(
            route = steps[currentStepIndex + 1].id,
            navOptions = navOptions {}, // TODO: Anything important/relevant needed here?
            navigatorExtras = null, // TODO: Anything important/relevant needed here?
        )
    }

    fun append(id: String) {
        val step = steps.firstOrNull { it.id == id } ?: error("")

        navController.navigate(
            route = step.id,
            navOptions = navOptions {}, // TODO: Anything important/relevant needed here?
            navigatorExtras = null, // TODO: Anything important/relevant needed here?
        )
    }

    fun append(content: @Composable () -> Unit) {
        val stepId = UUID().toString()
        customSteps.add(OnboardingStep(stepId, content))
        navController.navigate(
            route = customRoute(stepId),
            navOptions = navOptions {}, // TODO: Anything important/relevant needed here?
            navigatorExtras = null, // TODO: Anything important/relevant needed here?
        )
    }

    fun removeLast() {
        navController.navigateUp()
    }

    internal fun createGraph(): NavGraph {
        println("Recreating graph")

        val path = this
        return navController.createGraph(startDestination) {
            composable(
                route = customRoute("{id}"),
                arguments = listOf(navArgument("id") { type = NavType.StringType })
            ) { backStackEntry ->
                val step = backStackEntry.arguments?.getString("id")?.let { stepId ->
                    customSteps.firstOrNull { it.id == stepId }
                }
                CompositionLocalProvider(LocalOnboardingNavigationPath provides path) {
                    step?.content() ?: IllegalOnboardingStep()
                }
            }

            for (step in steps) {
                composable(step.id) {
                    CompositionLocalProvider(LocalOnboardingNavigationPath provides path) {
                        step.content()
                    }
                }
            }
        }
    }

    private fun customRoute(id: String) = "custom/$id"
}
