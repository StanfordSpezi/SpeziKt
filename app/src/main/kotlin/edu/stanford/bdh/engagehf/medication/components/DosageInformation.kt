package edu.stanford.bdh.engagehf.medication.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import edu.stanford.bdh.engagehf.medication.ui.DosageInformationUiModel
import edu.stanford.bdh.engagehf.medication.ui.DosageRowInfoData
import edu.stanford.spezi.modules.design.component.VerticalSpacer
import edu.stanford.spezi.ui.SpeziTheme
import edu.stanford.spezi.ui.TextStyles
import edu.stanford.spezi.ui.ThemePreviews

@Composable
fun DosageInformation(dosageInformationUiModel: DosageInformationUiModel) {
    Column {
        DosageInfoRow(dosageRowInfoData = dosageInformationUiModel.currentDose)
        VerticalSpacer()
        DosageInfoRow(dosageRowInfoData = dosageInformationUiModel.targetDose)
    }
}

@Composable
fun DosageInfoRow(dosageRowInfoData: DosageRowInfoData) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Column {
            Text(
                text = dosageRowInfoData.label,
                style = TextStyles.labelLarge
            )
        }
        Column {
            dosageRowInfoData.dosageValues.forEach { dose ->
                Text(
                    text = dose,
                    style = TextStyles.bodyMedium
                )
            }
        }
    }
}

private class DosageInformationProvider : PreviewParameterProvider<DosageInformationUiModel> {
    override val values: Sequence<DosageInformationUiModel> = sequenceOf(
        DosageInformationUiModel(
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
        )
    )
}

@ThemePreviews
@Composable
private fun DoseInformationPreview(
    @PreviewParameter(DosageInformationProvider::class) dosageInformation: DosageInformationUiModel,
) {
    SpeziTheme(isPreview = true) {
        DosageInformation(dosageInformationUiModel = dosageInformation)
    }
}
