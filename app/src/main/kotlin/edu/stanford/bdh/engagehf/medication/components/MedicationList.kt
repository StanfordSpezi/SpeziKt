package edu.stanford.bdh.engagehf.medication.components

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import edu.stanford.bdh.engagehf.R
import edu.stanford.bdh.engagehf.medication.ui.MedicationCardUiModel
import edu.stanford.bdh.engagehf.medication.ui.MedicationUiState
import edu.stanford.bdh.engagehf.medication.ui.MedicationViewModel
import edu.stanford.bdh.engagehf.medication.ui.Medications
import edu.stanford.spezi.ui.theme.SpeziTheme
import edu.stanford.spezi.ui.theme.ThemePreviews

@Composable
fun MedicationList(
    modifier: Modifier = Modifier,
    uiState: MedicationUiState.Success,
    onAction: (MedicationViewModel.Action) -> Unit,
) {
    LazyColumn(modifier = modifier) {
        medicationsSection(
            titleId = R.string.current_medications,
            medications = uiState.medicationsTaking,
            section = MedicationViewModel.Section.MEDICATIONS_TAKING,
            onAction = onAction,
        )

        medicationsSection(
            titleId = R.string.medications_that_may_help,
            medications = uiState.medicationsThatMayHelp,
            section = MedicationViewModel.Section.MEDICATIONS_THAT_MAY_HELP,
            onAction = onAction,
        )
        item {
            SectionHeader(
                title = stringResource(R.string.color_key_section_title),
                onToggleExpand = {
                    onAction(MedicationViewModel.Action.ToggleSectionExpand(MedicationViewModel.Section.COLOR_KEY))
                },
                isExpanded = uiState.colorKeyExpanded,
            )
        }
        if (uiState.colorKeyExpanded) {
            item { ColorKey() }
        }
    }
}

private fun LazyListScope.medicationsSection(
    titleId: Int,
    medications: Medications,
    section: MedicationViewModel.Section,
    onAction: (MedicationViewModel.Action) -> Unit,
) {
    if (medications.medications.isNotEmpty()) {
        item {
            SectionHeader(
                title = stringResource(titleId),
                onToggleExpand = {
                    onAction(MedicationViewModel.Action.ToggleSectionExpand(section))
                },
                isExpanded = medications.expanded,
            )
        }
        if (medications.expanded) {
            items(medications.medications) { model ->
                MedicationCard(model = model, onAction = onAction)
            }
        }
    }
}

@ThemePreviews
@Composable
private fun MedicationListPreview(
    @PreviewParameter(MedicationCardModelsProvider::class) uiModels: MedicationCardUiModel,
) {
    val uiState = MedicationUiState.Success(
        medicationsTaking = Medications(listOf(uiModels), true),
        medicationsThatMayHelp = Medications(emptyList(), true),
        colorKeyExpanded = true,
    )
    SpeziTheme {
        MedicationList(uiState = uiState, onAction = { })
    }
}
