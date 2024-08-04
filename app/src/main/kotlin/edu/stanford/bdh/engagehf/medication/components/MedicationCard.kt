package edu.stanford.bdh.engagehf.medication.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
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
import edu.stanford.bdh.engagehf.medication.MedicationDetails
import edu.stanford.bdh.engagehf.medication.MedicationScreenTestIdentifier
import edu.stanford.bdh.engagehf.medication.MedicationViewModel
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
        modifier = modifier.testIdentifier(
            identifier = MedicationScreenTestIdentifier.SUCCESS_MEDICATION_CARD_ROOT,
            suffix = medicationDetails.id
        ),
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
                        modifier = Modifier.testIdentifier(
                            identifier = MedicationScreenTestIdentifier.SUCCESS_MEDICATION_CARD_TITLE,
                            suffix = medicationDetails.id
                        )
                    )
                    Text(
                        text = medicationDetails.subtitle,
                        style = TextStyles.titleSmall,
                        modifier = Modifier.testIdentifier(
                            identifier = MedicationScreenTestIdentifier.SUCCESS_MEDICATION_CARD_SUBTITLE,
                            suffix = medicationDetails.id,
                        )
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
                            modifier = Modifier
                                .weight(1f)
                                .testIdentifier(
                                    identifier = MedicationScreenTestIdentifier.SUCCESS_MEDICATION_CARD_DESCRIPTION,
                                    suffix = medicationDetails.id
                                )
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

@ThemePreviews
@Composable
fun MedicationCardPreview(
    @PreviewParameter(MedicationDetailsProvider::class) medicationDetails: MedicationDetails,
) {
    SpeziTheme(isPreview = true) {
        MedicationCard(
            medicationDetails = medicationDetails,
            onAction = {}
        )
    }
}
