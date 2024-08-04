package edu.stanford.bdh.engagehf.medication

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.hilt.navigation.compose.hiltViewModel
import edu.stanford.bdh.engagehf.medication.components.MedicationList
import edu.stanford.bdh.engagehf.medication.components.getMedicationDetailsByStatus
import edu.stanford.spezi.core.design.theme.Colors
import edu.stanford.spezi.core.design.theme.Colors.primary
import edu.stanford.spezi.core.design.theme.SpeziTheme
import edu.stanford.spezi.core.design.theme.TextStyles
import edu.stanford.spezi.core.design.theme.ThemePreviews
import edu.stanford.spezi.core.utils.extensions.testIdentifier

@Composable
fun MedicationScreen() {
    val viewModel = hiltViewModel<MedicationViewModel>()
    val uiState by viewModel.uiState.collectAsState()
    MedicationScreen(
        uiState = uiState,
        onAction = viewModel::onAction
    )
}

@Composable
fun MedicationScreen(
    uiState: MedicationUiState,
    onAction: (MedicationViewModel.Action) -> Unit,
) {
    when (uiState) {
        is MedicationUiState.Error -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .testIdentifier(MedicationScreenTestIdentifier.ERROR_ROOT),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = (uiState).message,
                    color = Colors.error,
                    style = TextStyles.titleMedium,
                    modifier = Modifier.testIdentifier(MedicationScreenTestIdentifier.ERROR_TEXT),
                )
            }
        }

        MedicationUiState.Loading -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .testIdentifier(MedicationScreenTestIdentifier.LOADING),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(color = primary)
            }
        }

        is MedicationUiState.Success -> {
            MedicationList(
                uiState = uiState,
                onAction = onAction,
                modifier = Modifier.testIdentifier(MedicationScreenTestIdentifier.SUCCESS)
            )
        }
    }
}

private class UiStateProvider : PreviewParameterProvider<MedicationUiState> {
    override val values: Sequence<MedicationUiState> = sequenceOf(
        MedicationUiState.Loading,
        MedicationUiState.Error(message = "An error occurred"),
        MedicationUiState.Success(
            medicationDetails = listOf(
                getMedicationDetailsByStatus(
                    MedicationRecommendationType.TARGET_DOSE_REACHED,
                    isExpanded = true
                ),
                getMedicationDetailsByStatus(
                    MedicationRecommendationType.PERSONAL_TARGET_DOSE_REACHED,
                    isExpanded = true
                ),
                getMedicationDetailsByStatus(MedicationRecommendationType.IMPROVEMENT_AVAILABLE),
                getMedicationDetailsByStatus(MedicationRecommendationType.MORE_PATIENT_OBSERVATIONS_REQUIRED),
                getMedicationDetailsByStatus(MedicationRecommendationType.MORE_LAB_OBSERVATIONS_REQUIRED),
                getMedicationDetailsByStatus(MedicationRecommendationType.NOT_STARTED),
                getMedicationDetailsByStatus(MedicationRecommendationType.NO_ACTION_REQUIRED)
            )
        )
    )
}

enum class MedicationScreenTestIdentifier {
    LOADING,
    ERROR_ROOT,
    ERROR_TEXT,
    SUCCESS,
    SUCCESS_MEDICATION_CARD_ROOT,
    SUCCESS_MEDICATION_CARD_TITLE,
    SUCCESS_MEDICATION_CARD_SUBTITLE,
    SUCCESS_MEDICATION_CARD_DESCRIPTION,
}

@ThemePreviews
@Composable
private fun MedicationScreenPreview(@PreviewParameter(UiStateProvider::class) uiState: MedicationUiState) {
    SpeziTheme {
        MedicationScreen(
            uiState = uiState,
            onAction = { }
        )
    }
}
