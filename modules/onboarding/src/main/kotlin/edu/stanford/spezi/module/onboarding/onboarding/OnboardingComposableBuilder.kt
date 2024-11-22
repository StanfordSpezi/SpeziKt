package edu.stanford.spezi.module.onboarding.onboarding

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

data class OnboardingStep(
    val identifier: String,
    val composable: @Composable () -> Unit,
)

data class OnboardingComposableBuilder(
    var list: MutableList<OnboardingStep>,
) {
    fun step(id: String, composable: @Composable () -> Unit) {
        list.add(OnboardingStep(id, composable))
    }
}

fun buildOnboardingSteps(
    build: OnboardingComposableBuilder.() -> Unit,
): List<OnboardingStep> {
    val builder = OnboardingComposableBuilder(mutableListOf())
    build(builder)
    return builder.list
}

fun test() {
    buildOnboardingSteps {
        step("") {
            Text("")
        }

        val bool = true

        if (bool) {
            step("check") {
                Text("check")
            }
        }
    }
}
