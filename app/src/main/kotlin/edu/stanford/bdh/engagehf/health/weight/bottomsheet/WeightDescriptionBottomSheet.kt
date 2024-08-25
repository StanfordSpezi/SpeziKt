package edu.stanford.bdh.engagehf.health.weight.bottomsheet

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
import edu.stanford.spezi.core.design.theme.TextStyles

@Composable
fun WeightDescriptionBottomSheet() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(Spacings.medium),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Understanding Your Weight",
            style = TextStyles.titleLarge
        )
        VerticalSpacer()
        Text(
            text = "Your weight is a critical aspect of your overall health. It is influenced" +
                " by various factors including your diet, physical activity, genetics, and lifestyle habits.",
            style = TextStyles.bodyMedium.copy(textAlign = TextAlign.Center)
        )
        VerticalSpacer(height = Spacings.small)
        Text(
            text = "Maintaining a healthy weight can help you prevent and control many diseases and conditions. " +
                "It is important to balance the calories you consume with the calories you burn through activities.",
            style = TextStyles.bodyMedium.copy(textAlign = TextAlign.Center)
        )
        VerticalSpacer(height = Spacings.small)
        Text(
            text = "Regular exercise and a balanced diet are key components in managing your weight. " +
                "Consult with a healthcare provider to understand the ideal weight range for your body and health.",
            style = TextStyles.bodyMedium.copy(textAlign = TextAlign.Center)
        )
        VerticalSpacer()
    }
}
