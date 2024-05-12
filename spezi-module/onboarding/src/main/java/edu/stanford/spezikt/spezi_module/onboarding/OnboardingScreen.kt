package edu.stanford.spezikt.spezi_module.onboarding

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import edu.stanford.spezikt.core.design.theme.SpeziKtTheme

@Composable
fun OnboardingScreen() {
    Text(text = "Onboarding Screen")
}


@Preview
@Composable
fun OnboardingScreenPreview() {
    SpeziKtTheme {
        OnboardingScreen()
    }
}