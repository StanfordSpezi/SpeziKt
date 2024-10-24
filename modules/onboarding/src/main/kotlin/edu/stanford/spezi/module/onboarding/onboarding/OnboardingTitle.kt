package edu.stanford.spezi.module.onboarding.onboarding

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import edu.stanford.spezi.core.design.component.StringResource

@Composable
fun OnboardingTitle(title: StringResource, subtitle: StringResource? = null) {
    OnboardingTitle(title.text(), subtitle?.text())
}

@Composable
fun OnboardingTitle(title: String, subtitle: String? = null) {
    Column(Modifier.padding(vertical = 8.dp)) {
        Text(
            title,
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            textAlign = TextAlign.Center
        )

        subtitle?.let { subtitle ->
            Text(
                subtitle,
                modifier = Modifier.padding(bottom = 8.dp),
                textAlign = TextAlign.Center
            )
        }
    }
}
