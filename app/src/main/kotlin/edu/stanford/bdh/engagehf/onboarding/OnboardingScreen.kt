package edu.stanford.bdh.engagehf.onboarding

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import edu.stanford.bdh.engagehf.R
import edu.stanford.spezi.core.design.component.CommonScaffold
import edu.stanford.spezi.module.onboarding.onboarding.OnboardingView

@Composable
fun OnboardingScreen() {
    CommonScaffold(title = stringResource(R.string.onboarding)) {
        OnboardingView()
    }
}