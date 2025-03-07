package edu.stanford.bdh.engagehf.medication.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.hilt.navigation.compose.hiltViewModel
import edu.stanford.bdh.engagehf.medication.components.LoadingMedicationSection
import edu.stanford.bdh.engagehf.medication.components.MedicationList
import edu.stanford.bdh.engagehf.medication.components.getMedicationCardUiModel
import edu.stanford.spezi.core.design.component.CenteredBoxContent
import edu.stanford.spezi.core.design.component.RepeatingLazyColumn
import edu.stanford.spezi.spezi.ui.helpers.testIdentifier
import edu.stanford.spezi.spezi.ui.helpers.theme.Colors
import edu.stanford.spezi.spezi.ui.helpers.theme.Spacings
import edu.stanford.spezi.spezi.ui.helpers.theme.SpeziTheme
import edu.stanford.spezi.spezi.ui.helpers.theme.TextStyles

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
            RepeatingLazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(Spacings.medium)
                    .testIdentifier(MedicationScreenTestIdentifier.LOADING),
                itemCount = 2,
                content = { LoadingMedicationSection() }
            )
        }

        is MedicationUiState.Success -> {
            MedicationList(
                uiState = uiState,
                onAction = onAction,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(Spacings.medium)
                    .testIdentifier(MedicationScreenTestIdentifier.SUCCESS)
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
            medicationsTaking = Medications(
                listOf(
                    getMedicationCardUiModel(MedicationColor.YELLOW, true),
                    getMedicationCardUiModel(MedicationColor.GREEN_SUCCESS, true),
                ), expanded = true
            ),
            medicationsThatMayHelp = Medications(
                listOf(
                    getMedicationCardUiModel(MedicationColor.BLUE),
                ),
                expanded = false
            ),
            colorKeyExpanded = false
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
