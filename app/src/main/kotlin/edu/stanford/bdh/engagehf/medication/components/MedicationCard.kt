package edu.stanford.bdh.engagehf.medication.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewParameter
import edu.stanford.bdh.engagehf.medication.ui.MedicationCardUiModel
import edu.stanford.bdh.engagehf.medication.ui.MedicationScreenTestIdentifier
import edu.stanford.bdh.engagehf.medication.ui.MedicationViewModel
import edu.stanford.spezi.core.design.component.VerticalSpacer
import edu.stanford.spezi.core.design.theme.Colors
import edu.stanford.spezi.core.design.theme.Sizes
import edu.stanford.spezi.core.design.theme.Spacings
import edu.stanford.spezi.core.design.theme.SpeziTheme
import edu.stanford.spezi.core.design.theme.TextStyles
import edu.stanford.spezi.core.design.theme.ThemePreviews
import edu.stanford.spezi.core.design.theme.lighten
import edu.stanford.spezi.core.utils.extensions.testIdentifier

@Composable
fun MedicationCard(
    modifier: Modifier = Modifier,
    model: MedicationCardUiModel,
    onAction: (MedicationViewModel.Action) -> Unit,
) {
    ElevatedCard(
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = Sizes.Elevation.medium,
        ),
        colors = CardDefaults.cardColors(
            containerColor = Colors.surface.lighten(),
        ),
        modifier = modifier
            .testIdentifier(
                identifier = MedicationScreenTestIdentifier.SUCCESS_MEDICATION_CARD_ROOT,
                suffix = model.id
            )
            .clickable {
                onAction(
                    MedicationViewModel.Action.ToggleExpand(
                        medicationId = model.id
                    )
                )
            },
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacings.medium)
                .animateContentSize()
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                MedicationStatusIcon(model = model)
                Spacer(modifier = Modifier.width(Spacings.small))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = model.title,
                        style = TextStyles.titleLarge,
                        overflow = TextOverflow.Clip,
                        modifier = Modifier.testIdentifier(
                            identifier = MedicationScreenTestIdentifier.SUCCESS_MEDICATION_CARD_TITLE,
                            suffix = model.id
                        )
                    )
                    Text(
                        text = model.subtitle,
                        style = TextStyles.titleSmall,
                        overflow = TextOverflow.Clip,
                        modifier = Modifier.testIdentifier(
                            identifier = MedicationScreenTestIdentifier.SUCCESS_MEDICATION_CARD_SUBTITLE,
                            suffix = model.id,
                        )
                    )
                }
                IconButton(onClick = {
                    onAction(
                        MedicationViewModel.Action.ToggleExpand(
                            medicationId = model.id,
                        )
                    )
                }) {
                    Icon(
                        Icons.Default.ArrowDropDown,
                        contentDescription = null,
                    )
                }
            }
            if (model.isExpanded) {
                if (model.description.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(Spacings.small))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = model.description,
                            maxLines = Int.MAX_VALUE,
                            overflow = TextOverflow.Clip,
                            modifier = Modifier
                                .weight(1f)
                                .testIdentifier(
                                    identifier = MedicationScreenTestIdentifier.SUCCESS_MEDICATION_CARD_DESCRIPTION,
                                    suffix = model.id
                                )
                        )
                        model.videoPath?.let {
                            IconButton(onClick = {
                                onAction(MedicationViewModel.Action.InfoClicked(videoPath = it))
                            }) {
                                Icon(Icons.Filled.Info, contentDescription = "Information Icon")
                            }
                        }
                    }
                }
                if (model.dosageInformation != null) {
                    HorizontalDivider(
                        modifier = Modifier.padding(
                            top = Spacings.small,
                            bottom = Spacings.small
                        )
                    )
                    DosageInformation(dosageInformationUiModel = model.dosageInformation)
                    VerticalSpacer()
                    MedicationProgressBar(progress = model.dosageInformation.progress)
                }
            }
        }
    }
}

@ThemePreviews
@Composable
fun MedicationCardPreview(
    @PreviewParameter(MedicationCardModelsProvider::class) model: MedicationCardUiModel,
) {
    SpeziTheme(isPreview = true) {
        MedicationCard(
            model = model,
            onAction = {}
        )
    }
}
