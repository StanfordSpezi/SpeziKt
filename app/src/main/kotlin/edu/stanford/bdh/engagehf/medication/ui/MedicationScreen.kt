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
import edu.stanford.bdh.engagehf.R
import edu.stanford.bdh.engagehf.medication.components.LoadingMedicationSection
import edu.stanford.bdh.engagehf.medication.components.MedicationList
import edu.stanford.bdh.engagehf.medication.components.getMedicationCardUiModel
import edu.stanford.spezi.modules.design.component.CenteredBoxContent
import edu.stanford.spezi.modules.design.component.RepeatingLazyColumn
import edu.stanford.spezi.ui.Colors
import edu.stanford.spezi.ui.Spacings
import edu.stanford.spezi.ui.SpeziTheme
import edu.stanford.spezi.ui.StringResource
import edu.stanford.spezi.ui.TextStyles
import edu.stanford.spezi.ui.ThemePreviews
import edu.stanford.spezi.ui.testIdentifier

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
                    text = uiState.message.text(),
                    color = Colors.error,
                    style = TextStyles.titleMedium,
                    modifier = Modifier.testIdentifier(MedicationScreenTestIdentifier.ERROR_TEXT),
                )
            }
        }

        is MedicationUiState.NoData -> {
            CenteredBoxContent {
                Text(
                    text = uiState.message.text(),
                    style = TextStyles.titleMedium,
                    modifier = Modifier.testIdentifier(MedicationScreenTestIdentifier.NO_DATA_TEXT),
                )
            }
        }

        MedicationUiState.Loading -> {
            RepeatingLazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = Spacings.medium)
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
                    .padding(horizontal = Spacings.medium)
                    .testIdentifier(MedicationScreenTestIdentifier.SUCCESS)
            )
        }
    }
}

private class UiStateProvider : PreviewParameterProvider<MedicationUiState> {
    override val values: Sequence<MedicationUiState> = sequenceOf(
        MedicationUiState.Loading,
        MedicationUiState.Error(message = StringResource(R.string.generic_error_description)),
        MedicationUiState.NoData(message = StringResource(R.string.no_messages)),
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
