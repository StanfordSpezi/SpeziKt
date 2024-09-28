package edu.stanford.bdh.engagehf.health.weight.bottomsheet

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import edu.stanford.bdh.engagehf.R
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
            text = stringResource(R.string.weight_description_part_1),
            style = TextStyles.bodyMedium.copy(textAlign = TextAlign.Center)
        )
        VerticalSpacer(height = Spacings.small)
        Text(
            text = stringResource(R.string.weight_description_part_2),
            style = TextStyles.bodyMedium.copy(textAlign = TextAlign.Center)
        )
        VerticalSpacer(height = Spacings.small)
        Text(
            text = stringResource(R.string.weight_description_part_3),
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
