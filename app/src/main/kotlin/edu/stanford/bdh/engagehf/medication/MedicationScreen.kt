package edu.stanford.bdh.engagehf.medication

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.hilt.navigation.compose.hiltViewModel
import edu.stanford.bdh.engagehf.medication.components.DosageInformation
import edu.stanford.bdh.engagehf.medication.components.MedicationList
import edu.stanford.bdh.engagehf.medication.components.MedicationProgressBar
import edu.stanford.bdh.engagehf.medication.components.MedicationStatusIcon
import edu.stanford.bdh.engagehf.medication.components.getMedicationDetailsByStatus
import edu.stanford.spezi.core.design.component.VerticalSpacer
import edu.stanford.spezi.core.design.theme.Colors
import edu.stanford.spezi.core.design.theme.Colors.primary
import edu.stanford.spezi.core.design.theme.Sizes
import edu.stanford.spezi.core.design.theme.Spacings
import edu.stanford.spezi.core.design.theme.SpeziTheme
import edu.stanford.spezi.core.design.theme.TextStyles
import edu.stanford.spezi.core.design.theme.ThemePreviews
import edu.stanford.spezi.core.design.theme.lighten

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
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = (uiState).message,
                    color = Colors.error,
                    style = TextStyles.titleMedium,
                )
            }
        }

        MedicationUiState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(color = primary)
            }
        }

        is MedicationUiState.Success -> {
            MedicationList(
                uiState = uiState,
                onAction = onAction,
            )
        }
    }
}

@Composable
fun MedicationCard(
    modifier: Modifier = Modifier,
    medicationDetails: MedicationDetails,
    onAction: (MedicationViewModel.Action) -> Unit,
) {
    ElevatedCard(
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = Sizes.Elevation.medium,
        ),
        colors = CardDefaults.cardColors(
            containerColor = Colors.surface.lighten(isSystemInDarkTheme()),
        ),
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacings.medium)
                .animateContentSize()
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable {
                onAction(
                    MedicationViewModel.Action.ExpandMedication(
                        medicationId = medicationDetails.id,
                        isExpanded = !medicationDetails.isExpanded,
                    )
                )
            }) {
                MedicationStatusIcon(medicationDetails = medicationDetails)
                Spacer(modifier = Modifier.width(Spacings.small))
                Column {
                    Text(
                        text = medicationDetails.title,
                        style = TextStyles.titleLarge,
                    )
                    Text(
                        text = medicationDetails.subtitle,
                        style = TextStyles.titleSmall
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = {
                    onAction(
                        MedicationViewModel.Action.ExpandMedication(
                            medicationDetails.id,
                            !medicationDetails.isExpanded
                        )
                    )
                }) {
                    Icon(
                        Icons.Default.ArrowDropDown,
                        contentDescription = null,
                    )
                }
            }
            if (medicationDetails.isExpanded) {
                if (medicationDetails.description.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(Spacings.small))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = medicationDetails.description,
                            maxLines = Int.MAX_VALUE,
                            overflow = TextOverflow.Clip,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(onClick = { /*TODO*/ }) {
                            Icon(Icons.Filled.Info, contentDescription = "Information Icon")
                        }
                    }
                }
                if (medicationDetails.dosageInformation != null) {
                    HorizontalDivider(
                        modifier = Modifier.padding(
                            top = Spacings.small,
                            bottom = Spacings.small
                        )
                    )
                    DosageInformation(dosageInformation = medicationDetails.dosageInformation)
                    VerticalSpacer()
                    MedicationProgressBar(
                        currentProgress = medicationDetails.dosageInformation.currentDailyIntake.toFloat(),
                        targetProgress = medicationDetails.dosageInformation.targetDailyIntake.toFloat()
                    )
                }
            }
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
