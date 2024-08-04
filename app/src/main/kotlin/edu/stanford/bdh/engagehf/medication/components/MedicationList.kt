package edu.stanford.bdh.engagehf.medication.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import edu.stanford.bdh.engagehf.medication.MedicationCard
import edu.stanford.bdh.engagehf.medication.MedicationDetails
import edu.stanford.bdh.engagehf.medication.MedicationRecommendationType
import edu.stanford.bdh.engagehf.medication.MedicationUiState
import edu.stanford.bdh.engagehf.medication.MedicationViewModel
import edu.stanford.spezi.core.design.component.VerticalSpacer
import edu.stanford.spezi.core.design.theme.Spacings
import edu.stanford.spezi.core.design.theme.SpeziTheme
import edu.stanford.spezi.core.design.theme.ThemePreviews

@Composable
fun MedicationList(
    uiState: MedicationUiState.Success,
    onAction: (MedicationViewModel.Action) -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(Spacings.medium)
    ) {
        items(uiState.medicationDetails) {
            MedicationCard(medicationDetails = it, onAction = onAction)
            VerticalSpacer()
        }
    }
}

private class MedicationDetailsListProvider : PreviewParameterProvider<List<MedicationDetails>> {
    override val values: Sequence<List<MedicationDetails>> = sequenceOf(
        listOf(
            getMedicationDetailsByStatus(MedicationRecommendationType.TARGET_DOSE_REACHED).copy(
                isExpanded = true
            ),
            getMedicationDetailsByStatus(MedicationRecommendationType.PERSONAL_TARGET_DOSE_REACHED),
            getMedicationDetailsByStatus(MedicationRecommendationType.IMPROVEMENT_AVAILABLE),
        ),
        listOf(
            getMedicationDetailsByStatus(MedicationRecommendationType.MORE_PATIENT_OBSERVATIONS_REQUIRED),
            getMedicationDetailsByStatus(MedicationRecommendationType.MORE_LAB_OBSERVATIONS_REQUIRED).copy(
                isExpanded = true
            ),
            getMedicationDetailsByStatus(MedicationRecommendationType.NOT_STARTED),
            getMedicationDetailsByStatus(MedicationRecommendationType.NO_ACTION_REQUIRED)
        )
    )
}

@ThemePreviews
@Composable
private fun MedicationListPreview(
    @PreviewParameter(MedicationDetailsListProvider::class) medicationDetails: List<MedicationDetails>,
) {
    val uiState = MedicationUiState.Success(medicationDetails = medicationDetails)
    SpeziTheme {
        MedicationList(uiState = uiState, onAction = { })
    }
}
