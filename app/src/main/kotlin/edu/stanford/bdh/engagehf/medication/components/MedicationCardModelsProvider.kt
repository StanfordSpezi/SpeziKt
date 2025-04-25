package edu.stanford.bdh.engagehf.medication.components

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import edu.stanford.bdh.engagehf.medication.ui.DosageInformationUiModel
import edu.stanford.bdh.engagehf.medication.ui.DosageRowInfoData
import edu.stanford.bdh.engagehf.medication.ui.MedicationCardUiModel
import edu.stanford.bdh.engagehf.medication.ui.MedicationColor
import edu.stanford.spezi.modules.design.R

class MedicationCardModelsProvider : PreviewParameterProvider<MedicationCardUiModel> {
    override val values: Sequence<MedicationCardUiModel> = sequenceOf(
        getMedicationCardUiModel(MedicationColor.GREEN_SUCCESS, isExpanded = true),
        getMedicationCardUiModel(color = MedicationColor.GREEN_SUCCESS),
        getMedicationCardUiModel(color = MedicationColor.YELLOW),
        getMedicationCardUiModel(color = MedicationColor.BLUE),
    )
}

internal fun getMedicationCardUiModel(
    color: MedicationColor,
    isExpanded: Boolean = false,
) = MedicationCardUiModel(
    id = "1",
    title = "Medication 1",
    subtitle = "Subtitle 1",
    description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. " +
        "Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.",
    dosageInformation = DosageInformationUiModel(
        currentDose = DosageRowInfoData(
            label = "Current Dose:",
            dosageValues = listOf(
                "1.0 mg daily",
                "2.0 mg daily",
            )
        ),
        targetDose = DosageRowInfoData(
            label = "Target Dose:",
            dosageValues = listOf(
                "1.0 mg daily",
            )
        ),
        progress = 0.234f,
    ),
    isExpanded = isExpanded,
    statusColor = color,
    videoPath = "/videoSections/1/videos/1",
    statusIconResId = R.drawable.ic_check,
)
