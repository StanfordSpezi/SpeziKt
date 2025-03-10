package edu.stanford.bdh.engagehf.medication.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import edu.stanford.bdh.engagehf.R
import edu.stanford.bdh.engagehf.medication.ui.MedicationColor
import edu.stanford.spezi.ui.DefaultElevatedCard
import edu.stanford.spezi.ui.Sizes
import edu.stanford.spezi.ui.Spacings
import edu.stanford.spezi.ui.SpeziTheme
import edu.stanford.spezi.ui.ThemePreviews

@Composable
fun ColorKey(
    modifier: Modifier = Modifier,
) {
    DefaultElevatedCard(
        modifier = modifier
            .padding(bottom = Spacings.medium),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacings.medium),
            verticalArrangement = Arrangement.spacedBy(Spacings.small),
        ) {
            MedicationColor.entries.forEach {
                ColorKeyRow(color = it)
            }
        }
    }
}

@Composable
fun ColorKeyRow(color: MedicationColor) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(Sizes.Icon.small)
                .background(
                    color.value.copy(alpha = MEDICATION_ICON_ALPHA_COLOR_FACTOR),
                    shape = CircleShape
                )
                .padding(Spacings.small),
        )
        Spacer(modifier = Modifier.width(Spacings.small))
        Text(
            text = when (color) {
                MedicationColor.GREEN_SUCCESS -> stringResource(R.string.you_are_on_the_target_dose)
                MedicationColor.YELLOW -> stringResource(R.string.you_are_on_this_medication)
                MedicationColor.BLUE -> stringResource(R.string.more_information_needed)
                MedicationColor.GRAY -> stringResource(R.string.you_are_not_on_this_medication)
            },
            overflow = TextOverflow.Clip,
        )
    }
}

@ThemePreviews
@Composable
private fun ColorKeyPreview() {
    SpeziTheme(isPreview = true) {
        ColorKey()
    }
}
