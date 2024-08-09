package edu.stanford.bdh.engagehf.medication.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import edu.stanford.bdh.engagehf.R
import edu.stanford.bdh.engagehf.medication.DosageInformation
import edu.stanford.bdh.engagehf.medication.DoseSchedule
import edu.stanford.spezi.core.design.component.VerticalSpacer
import edu.stanford.spezi.core.design.theme.SpeziTheme
import edu.stanford.spezi.core.design.theme.TextStyles
import edu.stanford.spezi.core.design.theme.ThemePreviews

@Composable
fun DosageInformation(dosageInformation: DosageInformation) {
    Column {
        DoseRow(
            label = stringResource(R.string.dosage_information_dose_row_current_dose),
            dosageInformation = dosageInformation,
            doseSchedule = dosageInformation.currentSchedule
        )
        VerticalSpacer()
        DoseRow(
            label = stringResource(R.string.dosage_information_dose_row_target_dose),
            dosageInformation = dosageInformation,
            doseSchedule = dosageInformation.targetSchedule
        )
    }
}

@Composable
fun DoseRow(label: String, dosageInformation: DosageInformation, doseSchedule: List<DoseSchedule>) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Column {
            Text(
                text = label,
                style = TextStyles.labelLarge
            )
        }
        Column {
            doseSchedule.forEach { dose ->
                Text(
                    text = "${dose.totalDailyIntake} ${dosageInformation.unit} daily",
                    style = TextStyles.bodyMedium
                )
            }
        }
    }
}

@Suppress("MagicNumber")
private class DosageInformationProvider : PreviewParameterProvider<DosageInformation> {
    override val values: Sequence<DosageInformation> = sequenceOf(
        DosageInformation(
            currentSchedule = listOf(DoseSchedule(2.0, listOf(10.0))),
            minimumSchedule = emptyList(),
            targetSchedule = listOf(DoseSchedule(2.0, listOf(20.0))),
            unit = "mg"
        ),
        DosageInformation(
            currentSchedule = listOf(
                DoseSchedule(3.0, listOf(10.0, 5.0)),
                DoseSchedule(2.0, listOf(5.0))
            ),
            minimumSchedule = emptyList(),
            targetSchedule = listOf(DoseSchedule(3.0, listOf(20.0, 30.0))),
            unit = "mg"
        ),
    )
}

@ThemePreviews
@Composable
private fun DoseInformationPreview(
    @PreviewParameter(DosageInformationProvider::class) dosageInformation: DosageInformation,
) {
    SpeziTheme(isPreview = true) {
        DosageInformation(dosageInformation = dosageInformation)
    }
}
