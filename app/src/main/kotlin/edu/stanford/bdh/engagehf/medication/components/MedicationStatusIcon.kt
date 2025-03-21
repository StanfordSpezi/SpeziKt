package edu.stanford.bdh.engagehf.medication.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import edu.stanford.bdh.engagehf.medication.ui.MedicationCardUiModel
import edu.stanford.spezi.ui.Sizes
import edu.stanford.spezi.ui.Spacings
import edu.stanford.spezi.ui.SpeziTheme
import edu.stanford.spezi.ui.ThemePreviews

const val MEDICATION_ICON_ALPHA_COLOR_FACTOR = 0.3f

@Composable
fun MedicationStatusIcon(model: MedicationCardUiModel) {
    val backgroundColor = model.statusColor.value
    Box(
        modifier = Modifier
            .size(Sizes.Icon.medium)
            .background(
                backgroundColor.copy(alpha = MEDICATION_ICON_ALPHA_COLOR_FACTOR),
                shape = CircleShape
            )
            .padding(Spacings.small),
        contentAlignment = Alignment.Center
    ) {
        model.statusIconResId?.let {
            Icon(
                painter = painterResource(id = it),
                contentDescription = null,
                tint = backgroundColor,
                modifier = Modifier
                    .size(Sizes.Icon.medium)
                    .padding(Spacings.small)
            )
        }
    }
}

@ThemePreviews
@Composable
private fun MedicationStatusIconPreview(
    @PreviewParameter(MedicationCardModelsProvider::class) model: MedicationCardUiModel,
) {
    SpeziTheme(isPreview = true) {
        MedicationStatusIcon(model = model)
    }
}
