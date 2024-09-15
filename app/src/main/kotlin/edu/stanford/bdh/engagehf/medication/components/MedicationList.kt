package edu.stanford.bdh.engagehf.medication.components

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewParameter
import edu.stanford.bdh.engagehf.medication.ui.MedicationCardUiModel
import edu.stanford.bdh.engagehf.medication.ui.MedicationUiState
import edu.stanford.bdh.engagehf.medication.ui.MedicationViewModel
import edu.stanford.spezi.core.design.theme.SpeziTheme
import edu.stanford.spezi.core.design.theme.ThemePreviews

@Composable
fun MedicationList(
    modifier: Modifier = Modifier,
    uiState: MedicationUiState.Success,
    onAction: (MedicationViewModel.Action) -> Unit,
) {
    LazyColumn(modifier = modifier) {
        items(uiState.uiModels) {
            MedicationCard(model = it, onAction = onAction)
        }
    }
}

@ThemePreviews
@Composable
private fun MedicationListPreview(
    @PreviewParameter(MedicationCardModelsProvider::class) uiModels: List<MedicationCardUiModel>,
) {
    val uiState = MedicationUiState.Success(uiModels = uiModels)
    SpeziTheme {
        MedicationList(uiState = uiState, onAction = { })
    }
}