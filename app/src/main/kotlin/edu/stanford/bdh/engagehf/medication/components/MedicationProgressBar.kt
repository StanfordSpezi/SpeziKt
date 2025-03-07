package edu.stanford.bdh.engagehf.medication.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import edu.stanford.bdh.engagehf.R
import edu.stanford.spezi.core.design.component.VerticalSpacer
import edu.stanford.spezi.spezi.ui.helpers.theme.Sizes
import edu.stanford.spezi.spezi.ui.helpers.theme.Spacings
import edu.stanford.spezi.spezi.ui.helpers.theme.SpeziTheme

private const val PROGRESS_GREEN_HEX = 0xFF00796B
private val PROGRESS_GREEN = Color(PROGRESS_GREEN_HEX)

@Composable
fun MedicationProgressBar(progress: Float) {
    Column {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(Sizes.Content.small)
                .background(Color.LightGray, RoundedCornerShape(Sizes.RoundedCorner.large))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(progress)
                    .background(PROGRESS_GREEN, RoundedCornerShape(Sizes.RoundedCorner.large))
            )
        }
        VerticalSpacer(height = Spacings.extraSmall)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(R.string.medication_progress_bar_current),
                color = PROGRESS_GREEN,
            )
            Text(
                text = stringResource(R.string.medication_progress_bar_target),
                color = Color.LightGray,
            )
        }
    }
}

@Suppress("MagicNumber")
private class MedicationProgressBarProvider : PreviewParameterProvider<Float> {
    override val values: Sequence<Float> = sequenceOf(
        0f,
        0.5f,
        0.75f,
        1f,
    )
}

@ThemePreviews
@Composable
private fun MedicationProgressBarPreview(
    @PreviewParameter(MedicationProgressBarProvider::class) progress: Float,
) {
    SpeziTheme(isPreview = true) {
        MedicationProgressBar(progress = progress)
    }
}
