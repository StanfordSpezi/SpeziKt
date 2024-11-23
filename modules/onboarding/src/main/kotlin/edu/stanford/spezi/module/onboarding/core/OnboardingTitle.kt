package edu.stanford.spezi.module.onboarding.core

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import edu.stanford.spezi.core.design.theme.Spacings
import edu.stanford.spezi.core.design.theme.SpeziTheme
import edu.stanford.spezi.core.design.theme.TextStyles
import edu.stanford.spezi.core.design.theme.ThemePreviews

@Composable
fun OnboardingTitle(
    title: String,
    subtitle: String? = null,
) {
    Column(Modifier.padding(vertical = Spacings.medium), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            title,
            modifier = Modifier.padding(bottom = Spacings.medium),
            style = TextStyles.headlineMedium.copy(fontWeight = FontWeight.Bold),
            textAlign = TextAlign.Center
        )

        subtitle?.let { subtitle ->
            Text(
                subtitle,
                modifier = Modifier.padding(bottom = Spacings.medium),
                style = TextStyles.bodyMedium,
                textAlign = TextAlign.Center
            )
        }
    }
}

@ThemePreviews
@Composable
private fun OnboardingTitlePreview() {
    SpeziTheme(isPreview = true) {
        OnboardingTitle("Title", "Subtitle")
    }
}
