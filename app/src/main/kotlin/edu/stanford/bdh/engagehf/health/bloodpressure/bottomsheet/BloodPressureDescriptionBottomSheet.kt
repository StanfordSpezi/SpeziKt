package edu.stanford.bdh.engagehf.health.bloodpressure.bottomsheet

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
fun BloodPressureDescriptionBottomSheet() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(Spacings.medium),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Understanding Your Blood Pressure",
            style = TextStyles.titleLarge
        )
        VerticalSpacer()
        Text(
            text = "Blood pressure is the force of blood against the walls of your arteries. " +
                "It is measured in millimeters of mercury (mmHg) and consists of two numbers: systolic and diastolic.",
            style = TextStyles.bodyMedium.copy(textAlign = TextAlign.Center)
        )
        VerticalSpacer(height = Spacings.small)
        Text(
            text = "A normal blood pressure reading is less than 120/80 mmHg. " +
                "High blood pressure (hypertension) is a common condition that can lead to serious health problems.",
            style = TextStyles.bodyMedium.copy(textAlign = TextAlign.Center)
        )
        VerticalSpacer(height = Spacings.small)
        Text(
            text = "Regular exercise, a healthy diet, and stress management can help maintain a healthy blood pressure. " +
                "Consult with a healthcare provider to understand your target blood pressure and how to monitor it.",
            style = TextStyles.bodyMedium.copy(textAlign = TextAlign.Center)
        )
        VerticalSpacer()
    }
}

@ThemePreviews
@Composable
fun BloodPressureDescriptionBottomSheetPreview() {
    SpeziTheme(isPreview = true) {
        BloodPressureDescriptionBottomSheet()
    }
}
