package edu.stanford.bdh.engagehf.medication.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
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
import edu.stanford.spezi.core.design.component.CircularShimmerEffect
import edu.stanford.spezi.core.design.component.RectangleShimmerEffect
import edu.stanford.spezi.core.design.component.VerticalSpacer
import edu.stanford.spezi.core.design.theme.Colors
import edu.stanford.spezi.core.design.theme.Sizes
import edu.stanford.spezi.core.design.theme.Spacings
import edu.stanford.spezi.core.design.theme.SpeziTheme
import edu.stanford.spezi.core.design.theme.TextStyles
import edu.stanford.spezi.core.design.theme.ThemePreviews
import edu.stanford.spezi.core.utils.extensions.testIdentifier

private const val LOADING_CARDS = 6

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
            LazyColumn(
                modifier = Modifier
                    .testIdentifier(MedicationScreenTestIdentifier.LOADING)
                    .fillMaxSize()
            ) {
                items(LOADING_CARDS) { LoadingMedicationCard() }
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

@Composable
private fun LoadingMedicationCard() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(Spacings.medium)
    ) {
        CircularShimmerEffect(modifier = Modifier.size(Sizes.Content.large))

        Column(modifier = Modifier.padding(Spacings.small)) {
            RectangleShimmerEffect(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(Sizes.Content.small)
            )
            VerticalSpacer()
            RectangleShimmerEffect(
                modifier = Modifier
                    .fillMaxWidth(fraction = 0.5f)
                    .height(Sizes.Content.small)
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
