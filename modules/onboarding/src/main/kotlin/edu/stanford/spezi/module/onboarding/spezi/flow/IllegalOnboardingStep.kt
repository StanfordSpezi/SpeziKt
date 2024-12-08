package edu.stanford.spezi.module.onboarding.spezi.flow

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import edu.stanford.spezi.core.design.component.StringResource
import edu.stanford.spezi.core.design.component.StringResource.Companion.invoke

@Composable
internal fun IllegalOnboardingStep() {
    Text(StringResource("ILLEGAL_ONBOARDING_STEP").text())
}
