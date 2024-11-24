package edu.stanford.spezi.module.onboarding.spezi.flow

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.createGraph
import edu.stanford.spezi.core.design.component.Button
import edu.stanford.spezi.core.design.theme.SpeziTheme
import edu.stanford.spezi.core.design.theme.ThemePreviews

internal data class OnboardingNavigationStep(
    val id: String,
    val content: @Composable () -> Unit,
)

val LocalOnboardingNavigationPath = compositionLocalOf<OnboardingNavigationPath> {
    error("Do not access this outside of OnboardingStack!")
}

@Composable
fun OnboardingStack(
    modifier: Modifier = Modifier,
    onComplete: () -> Unit = {},
    startAtStep: String? = null,
    content: OnboardingStackBuilder.() -> Unit,
) {
    val steps = OnboardingStackBuilder().apply { content() }.steps
    val navController = rememberNavController()
    val startDestination = remember(startAtStep, steps) {
        startAtStep ?: steps.firstOrNull()?.id ?: error("No step specified")
    }
    val navigationPath = remember {
        OnboardingNavigationPath(navController, startDestination, steps)
    }

    LaunchedEffect(startDestination) {
        navigationPath.startDestination = startDestination
    }

    LaunchedEffect(steps) {
        navigationPath.steps = steps
    }

    NavHost(
        navController = navController,
        graph = remember(startDestination, steps) {
            navigationPath.createGraph()
        },
        modifier = modifier,
    )

    LaunchedEffect(navController) {
        navController.currentBackStackEntryFlow.collect { entry ->
            if (entry.destination.route == null) {
                onComplete()
            }
        }
    }
}

@Composable
private fun OnboardingStepPreview(index: Int) {
    val path = LocalOnboardingNavigationPath.current

    Column {
        Text("Page $index")

        Button(onClick = {
            path.removeLast()
        }) {
            Text("Remove Last")
        }

        Button(onClick = {
            path.nextStep()
        }) {
            Text("Next")
        }
    }
}

@ThemePreviews
@Composable
private fun OnboardingStackPreview() {
    SpeziTheme(isPreview = true) {
        OnboardingStack {
            step("1") {
                OnboardingStepPreview(1)
            }
            step("2") {
                OnboardingStepPreview(2)
            }
            step("3") {
                @Suppress("detekt:MagicNumber")
                OnboardingStepPreview(3)
            }
        }
    }
}
