package edu.stanford.spezi.module.onboarding.spezi.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import edu.stanford.spezi.module.onboarding.core.OnboardingTitle
import edu.stanford.spezi.module.onboarding.spezi.flow.LocalOnboardingNavigationPath
import edu.stanford.spezi.module.onboarding.spezi.flow.OnboardingStack

object DynamicOnboardingTestStepId {
    const val START = "START"
    const val ONE = "ONE"
    const val TWO = "TWO"
    const val DONE = "DONE"
}

@Composable
fun DynamicOnboardingTestComposable() {
    OnboardingStack {
        step(DynamicOnboardingTestStepId.START) {
            val path = LocalOnboardingNavigationPath.current

            Column {
                Text("START")

                Button(onClick = {
                    path.append(DynamicOnboardingTestStepId.ONE)
                }) {
                    Text("ONE")
                }

                Button(onClick = {
                    path.append(DynamicOnboardingTestStepId.TWO)
                }) {
                    Text("TWO")
                }

                Button(onClick = {
                    path.append {
                        DynamicOnboardingComposable("THREE")
                    }
                }) {
                    Text("THREE")
                }

                Button(onClick = {
                    path.nextStep()
                }) {
                    Text("Next")
                }
            }
        }

        step(DynamicOnboardingTestStepId.ONE) {
            DynamicOnboardingComposable(DynamicOnboardingTestStepId.ONE)
        }

        step(DynamicOnboardingTestStepId.TWO) {
            DynamicOnboardingComposable(DynamicOnboardingTestStepId.TWO)
        }

        step(DynamicOnboardingTestStepId.DONE) {
            OnboardingTitle(
                "Done",
                "Dynamic Onboarding done!"
            )
        }
    }
}

@Composable
private fun DynamicOnboardingComposable(title: String) {
    val path = LocalOnboardingNavigationPath.current

    Column {
        Text("TITLE: $title")
        Button(onClick = {
            path.nextStep()
        }) {
            Text("Next")
        }
    }
}

