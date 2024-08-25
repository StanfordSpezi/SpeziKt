package edu.stanford.bdh.engagehf.medication.ui

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.hilt.navigation.compose.hiltViewModel
import edu.stanford.bdh.engagehf.medication.components.MedicationList
import edu.stanford.bdh.engagehf.medication.components.getMedicationCardUiModel
import edu.stanford.spezi.core.design.component.CenteredBoxContent
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
            CenteredBoxContent {
                Text(
                    text = (uiState).message,
                    color = Colors.error,
                    style = TextStyles.titleMedium,
                    modifier = Modifier.testIdentifier(MedicationScreenTestIdentifier.ERROR_TEXT),
                )
            }
        }

        is MedicationUiState.NoData -> {
            CenteredBoxContent {
                Text(
                    text = uiState.message,
                    style = TextStyles.titleMedium,
                    modifier = Modifier.testIdentifier(MedicationScreenTestIdentifier.NO_DATA_TEXT),
                )
            }
        }

        MedicationUiState.Loading -> {
            CenteredBoxContent {
                CircularProgressIndicator(
                    modifier = Modifier
                        .testIdentifier(MedicationScreenTestIdentifier.LOADING),
                    color = primary
                )
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
        MedicationUiState.NoData(message = "No message recommendations"),
        MedicationUiState.Success(
            uiModels = listOf(
                getMedicationCardUiModel(MedicationColor.YELLOW, true),
                getMedicationCardUiModel(MedicationColor.GREEN_SUCCESS, true),
                getMedicationCardUiModel(MedicationColor.GREY),
            )
        )
    )
}

enum class MedicationScreenTestIdentifier {
    LOADING,
    ERROR_TEXT,
    NO_DATA_TEXT,
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
