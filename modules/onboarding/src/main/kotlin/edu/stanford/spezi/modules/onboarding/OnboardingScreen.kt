package edu.stanford.spezi.modules.onboarding

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import edu.stanford.spezi.core.design.theme.SpeziTheme

@Composable
fun OnboardingScreen() {
    Text(text = "Onboarding Screen")
}


@Preview
@Composable
fun OnboardingScreenPreview() {
    SpeziTheme {
        OnboardingScreen()
    }
}