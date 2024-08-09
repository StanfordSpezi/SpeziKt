package edu.stanford.bdh.engagehf.medication.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import edu.stanford.bdh.engagehf.medication.MedicationDetails
import edu.stanford.spezi.core.design.theme.Sizes
import edu.stanford.spezi.core.design.theme.Spacings
import edu.stanford.spezi.core.design.theme.SpeziTheme
import edu.stanford.spezi.core.design.theme.ThemePreviews

@Composable
fun MedicationStatusIcon(medicationDetails: MedicationDetails) {
    val (iconRes, color) = medicationDetails.statusIconAndColor

    iconRes?.let {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = null,
            tint = color,
            modifier = Modifier
                .size(Sizes.Icon.medium)
                .background(color.copy(alpha = 0.1f), shape = CircleShape)
                .padding(Spacings.small)
        )
    } ?: run {
        Box(
            modifier = Modifier
                .size(Sizes.Icon.medium)
                .background(color.copy(alpha = 0.1f), shape = CircleShape)
                .padding(Spacings.small)
        )
    }
}

@ThemePreviews
@Composable
private fun MedicationStatusIconPreview(
    @PreviewParameter(MedicationDetailsProvider::class) medicationDetails: MedicationDetails,
) {
    SpeziTheme(isPreview = true) {
        MedicationStatusIcon(medicationDetails = medicationDetails)
    }
}
