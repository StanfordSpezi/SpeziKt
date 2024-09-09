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
import edu.stanford.spezi.core.design.theme.SpeziTheme
import edu.stanford.spezi.core.design.theme.TextStyles
import edu.stanford.spezi.core.design.theme.ThemePreviews

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
            text = "When the heart is weak, your body can hold onto more salt and water and your " +
                "weight can rise quickly. This can lead to trouble breathing, leg swelling, and stomach bloating.",
            style = TextStyles.bodyMedium.copy(textAlign = TextAlign.Center)
        )
        VerticalSpacer(height = Spacings.small)
        Text(
            text = "Small changes in weight are normal. But if you gain over 3 pounds in a day " +
                "or over 5 pounds in a week, talk to your care team.",
            style = TextStyles.bodyMedium.copy(textAlign = TextAlign.Center)
        )
        VerticalSpacer(height = Spacings.small)
        Text(
            text = "Check your weight every day at the same time of day to see if there may be fluid buildup. " +
                "For more info, see the weight video.",
            style = TextStyles.bodyMedium.copy(textAlign = TextAlign.Center)
        )
        VerticalSpacer()
    }
}

@ThemePreviews
@Composable
private fun WeightDescriptionBottomSheetPreview() {
    SpeziTheme(isPreview = true) {
        WeightDescriptionBottomSheet()
    }
}
