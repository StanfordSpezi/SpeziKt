package edu.stanford.bdh.engagehf.medication.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import edu.stanford.bdh.engagehf.R
import edu.stanford.bdh.engagehf.medication.ui.MedicationCardUiModel
import edu.stanford.bdh.engagehf.medication.ui.MedicationUiState
import edu.stanford.bdh.engagehf.medication.ui.MedicationViewModel
import edu.stanford.bdh.engagehf.medication.ui.Medications
import edu.stanford.spezi.core.design.component.CenteredBoxContent
import edu.stanford.spezi.core.design.component.DefaultElevatedCard
import edu.stanford.spezi.core.design.theme.Spacings
import edu.stanford.spezi.core.design.theme.SpeziTheme
import edu.stanford.spezi.core.design.theme.TextStyles
import edu.stanford.spezi.core.design.theme.ThemePreviews

@Composable
fun MedicationList(
    modifier: Modifier = Modifier,
    uiState: MedicationUiState.Success,
    onAction: (MedicationViewModel.Action) -> Unit,
) {
    LazyColumn(modifier = modifier) {
        item {
            SectionHeader(
                title = stringResource(R.string.current_medications),
                onToggleExpand = {
                    onAction(MedicationViewModel.Action.ToggleSectionExpand(MedicationViewModel.Section.MEDICATIONS_TAKING))
                },
                isExpanded = uiState.medicationsTaking.expanded,
            )
        }
        medicationItems(
            isExpanded = uiState.medicationsTaking.expanded,
            medications = uiState.medicationsTaking.medications,
            onAction = onAction,
        )
        item {
            SectionHeader(
                title = stringResource(R.string.medications_that_may_help),
                onToggleExpand = {
                    onAction(MedicationViewModel.Action.ToggleSectionExpand(MedicationViewModel.Section.MEDICATIONS_THAT_MAY_HELP))
                },
                isExpanded = uiState.medicationsThatMayHelp.expanded,
            )
        }
        medicationItems(
            isExpanded = uiState.medicationsThatMayHelp.expanded,
            medications = uiState.medicationsThatMayHelp.medications,
            onAction = onAction,
        )
        item {
            SectionHeader(
                title = stringResource(R.string.color_key),
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

fun LazyListScope.medicationItems(
    isExpanded: Boolean,
    medications: List<MedicationCardUiModel>,
    onAction: (MedicationViewModel.Action) -> Unit,
) {
    if (isExpanded) {
        if (medications.isEmpty()) {
            item {
                DefaultElevatedCard {
                    CenteredBoxContent {
                        Text(
                            modifier = Modifier.padding(Spacings.medium),
                            text = stringResource(R.string.no_medications_to_show),
                            style = TextStyles.bodyMedium,
                        )
                    }
                }
            }
        } else {
            items(medications) { model ->
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
