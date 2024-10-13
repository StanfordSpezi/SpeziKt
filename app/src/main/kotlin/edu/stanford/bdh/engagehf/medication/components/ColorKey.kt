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
import edu.stanford.spezi.core.design.component.DefaultElevatedCard
import edu.stanford.spezi.core.design.theme.Sizes
import edu.stanford.spezi.core.design.theme.Spacings
import edu.stanford.spezi.core.design.theme.SpeziTheme
import edu.stanford.spezi.core.design.theme.ThemePreviews

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
                .size(Sizes.Icon.medium)
                .background(
                    color.value.copy(alpha = MEDICATION_ICON_ALPHA_COLOR_FACTOR),
                    shape = CircleShape
                )
                .padding(Spacings.small),
        )
        Spacer(modifier = Modifier.width(Spacings.small))
        Text(
            text = when (color) {
                MedicationColor.GREEN_SUCCESS -> stringResource(R.string.on_target_dose_that_best_helps_your_heart)
                MedicationColor.YELLOW -> stringResource(R.string.on_medication_but_may_benefit_from_a_higher_dose)
                MedicationColor.BLUE -> stringResource(R.string.not_on_this_medication_that_may_help_your_heart)
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
