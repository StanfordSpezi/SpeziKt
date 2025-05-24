package edu.stanford.spezi.onboarding

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import edu.stanford.spezi.ui.theme.SpeziTheme
import edu.stanford.spezi.ui.theme.ThemePreviews

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
    SpeziTheme {
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
