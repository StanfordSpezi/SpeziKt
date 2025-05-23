package edu.stanford.spezi.onboarding

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import edu.stanford.spezi.ui.StringResource

@Composable
internal fun IllegalOnboardingStep() {
    Text(StringResource("ILLEGAL_ONBOARDING_STEP").text())
}
