package edu.stanford.bdh.engagehf.health.heartrate.bottomsheet

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import edu.stanford.spezi.core.design.component.VerticalSpacer
import edu.stanford.spezi.core.design.theme.Spacings
import edu.stanford.spezi.core.design.theme.SpeziTheme
import edu.stanford.spezi.core.design.theme.TextStyles
import edu.stanford.spezi.core.design.theme.ThemePreviews

@Composable
fun HeartRateDescriptionBottomSheet() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(Spacings.medium),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Understanding Your Heart Rate",
            style = TextStyles.titleLarge
        )
        VerticalSpacer()
        Text(
            text = "Your heart rate is the number of times your heart beats per minute. " +
                "It is a good indicator of your overall health and fitness level.",
            style = TextStyles.bodyMedium.copy(textAlign = TextAlign.Center)
        )
        VerticalSpacer(height = Spacings.small)
        Text(
            text = "A normal resting heart rate for adults ranges from 60 to 100 beats per minute. " +
                "Factors such as age, fitness level, and medications can affect your heart rate.",
            style = TextStyles.bodyMedium.copy(textAlign = TextAlign.Center)
        )
        VerticalSpacer(height = Spacings.small)
        Text(
            text = "Regular physical activity, a healthy diet, and stress management can help maintain a healthy heart rate. " +
                "Consult with a healthcare provider to understand your target heart rate and how to monitor it.",
            style = TextStyles.bodyMedium.copy(textAlign = TextAlign.Center)
        )
        VerticalSpacer()
    }
}

@ThemePreviews
@Composable
fun HeartRateDescriptionBottomSheetPreview() {
    SpeziTheme(isPreview = true) {
        HeartRateDescriptionBottomSheet()
    }
}
