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
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import edu.stanford.bdh.engagehf.medication.DosageInformation
import edu.stanford.bdh.engagehf.medication.DoseSchedule
import edu.stanford.bdh.engagehf.medication.MedicationDetails
import edu.stanford.bdh.engagehf.medication.MedicationRecommendationType
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

private class MedicationDetailsProvider : PreviewParameterProvider<MedicationDetails> {
    override val values: Sequence<MedicationDetails> = sequenceOf(
        getMedicationDetailsByStatus(MedicationRecommendationType.TARGET_DOSE_REACHED),
        getMedicationDetailsByStatus(MedicationRecommendationType.PERSONAL_TARGET_DOSE_REACHED),
        getMedicationDetailsByStatus(MedicationRecommendationType.IMPROVEMENT_AVAILABLE),
        getMedicationDetailsByStatus(MedicationRecommendationType.MORE_PATIENT_OBSERVATIONS_REQUIRED),
        getMedicationDetailsByStatus(MedicationRecommendationType.MORE_LAB_OBSERVATIONS_REQUIRED),
        getMedicationDetailsByStatus(MedicationRecommendationType.NOT_STARTED),
        getMedicationDetailsByStatus(MedicationRecommendationType.NO_ACTION_REQUIRED)
    )
}

@ThemePreviews
@Composable
private fun MedicationStatusIconPreview(@PreviewParameter(MedicationDetailsProvider::class) medicationDetails: MedicationDetails) {
    SpeziTheme(isPreview = true) {
        MedicationStatusIcon(medicationDetails = medicationDetails)
    }
}

internal fun getMedicationDetailsByStatus(
    status: MedicationRecommendationType,
    isExpanded: Boolean = false,
) =
    MedicationDetails(
        id = "1",
        title = "Medication 1",
        subtitle = "Subtitle 1",
        description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. " +
            "Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.",
        type = status,
        dosageInformation = DosageInformation(
            currentSchedule = listOf(
                DoseSchedule(
                    frequency = 1.0,
                    dosage = listOf(1.0),
                ),
            ),
            minimumSchedule = listOf(
                DoseSchedule(
                    frequency = 1.0,
                    dosage = listOf(1.0),
                ),
            ),
            targetSchedule = listOf(
                DoseSchedule(
                    frequency = 1.0,
                    dosage = listOf(2.0),
                ),
            ),
            unit = "mg",
        ),
        isExpanded = isExpanded,
    )
