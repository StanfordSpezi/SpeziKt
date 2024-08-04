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
import edu.stanford.bdh.engagehf.medication.MedicationViewModel.Companion.CoolGrey
import edu.stanford.bdh.engagehf.medication.MedicationViewModel.Companion.GreenProgress
import edu.stanford.spezi.core.design.component.VerticalSpacer
import edu.stanford.spezi.core.design.theme.Sizes
import edu.stanford.spezi.core.design.theme.Spacings
import edu.stanford.spezi.core.design.theme.SpeziTheme
import edu.stanford.spezi.core.design.theme.ThemePreviews

@Composable
fun MedicationProgressBar(currentProgress: Float, targetProgress: Float) {
    val progress = (currentProgress / targetProgress).coerceIn(0f, 1f)

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
                    .background(GreenProgress, RoundedCornerShape(Sizes.RoundedCorner.large))
            )
        }
        VerticalSpacer(height = Spacings.extraSmall)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(R.string.medication_progress_bar_current),
                color = GreenProgress,
            )
            Text(
                text = stringResource(R.string.medication_progress_bar_target),
                color = CoolGrey,
            )
        }
    }
}

@Suppress("MagicNumber")
private class MedicationProgressBarProvider : PreviewParameterProvider<Pair<Float, Float>> {
    override val values: Sequence<Pair<Float, Float>> = sequenceOf(
        Pair(0f, 100f),
        Pair(50f, 100f),
        Pair(75f, 100f),
        Pair(100f, 100f)
    )
}

@ThemePreviews
@Composable
private fun MedicationProgressBarPreview(
    @PreviewParameter(MedicationProgressBarProvider::class) progress: Pair<Float, Float>,
) {
    SpeziTheme(isPreview = true) {
        MedicationProgressBar(
            currentProgress = progress.first,
            targetProgress = progress.second
        )
    }
}
