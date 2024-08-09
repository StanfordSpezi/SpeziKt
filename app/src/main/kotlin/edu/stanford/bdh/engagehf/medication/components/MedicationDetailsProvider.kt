package edu.stanford.bdh.engagehf.medication.components

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import edu.stanford.bdh.engagehf.medication.DoseSchedule
import edu.stanford.bdh.engagehf.medication.MedicationDetails
import edu.stanford.bdh.engagehf.medication.MedicationRecommendationType

class MedicationDetailsProvider : PreviewParameterProvider<MedicationDetails> {
    override val values: Sequence<MedicationDetails> = sequenceOf(
        getMedicationDetailsByStatus(MedicationRecommendationType.TARGET_DOSE_REACHED).copy(
            isExpanded = true
        ),
        getMedicationDetailsByStatus(MedicationRecommendationType.PERSONAL_TARGET_DOSE_REACHED),
        getMedicationDetailsByStatus(MedicationRecommendationType.IMPROVEMENT_AVAILABLE),
        getMedicationDetailsByStatus(MedicationRecommendationType.MORE_PATIENT_OBSERVATIONS_REQUIRED),
        getMedicationDetailsByStatus(MedicationRecommendationType.MORE_LAB_OBSERVATIONS_REQUIRED),
        getMedicationDetailsByStatus(MedicationRecommendationType.NOT_STARTED),
        getMedicationDetailsByStatus(MedicationRecommendationType.NO_ACTION_REQUIRED)
    )
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
        dosageInformation = edu.stanford.bdh.engagehf.medication.DosageInformation(
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
