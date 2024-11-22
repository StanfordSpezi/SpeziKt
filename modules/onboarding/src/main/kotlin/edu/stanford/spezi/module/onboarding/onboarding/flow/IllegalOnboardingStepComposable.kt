package edu.stanford.spezi.module.onboarding.onboarding.flow

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import edu.stanford.spezi.core.design.component.StringResource

@Composable
internal fun IllegalOnboardingStepComposable() {
    Text(StringResource("Illegal onboarding step").text())
}
