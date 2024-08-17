package edu.stanford.bdh.engagehf.health.bloodpressure.bottomsheet

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import edu.stanford.bdh.engagehf.R
import edu.stanford.spezi.core.design.component.VerticalSpacer
import edu.stanford.spezi.core.design.theme.Colors
import edu.stanford.spezi.core.design.theme.Spacings
import edu.stanford.spezi.core.design.theme.SpeziTheme
import edu.stanford.spezi.core.design.theme.TextStyles
import edu.stanford.spezi.core.design.theme.ThemePreviews

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BodyPositionsDialog(
    onDismissRequest: () -> Unit,
    onOptionSelected: (BodyPositions) -> Unit,
    bodyPositions: List<BodyPositions>,
) {
    BasicAlertDialog(
        modifier = Modifier
            .background(Colors.surface, shape = RoundedCornerShape(Spacings.medium))
            .padding(Spacings.medium),
        onDismissRequest = onDismissRequest,
        content = {
            Column {
                Text(
                    text = stringResource(R.string.body_position),
                    style = TextStyles.titleLarge,
                )
                VerticalSpacer()
                bodyPositions.forEach { position ->
                    Text(
                        text = when (position) {
                            BodyPositions.BODY_POSITION_UNKNOWN -> stringResource(R.string.not_set)
                            BodyPositions.BODY_POSITION_STANDING_UP -> stringResource(R.string.standing_up)
                            BodyPositions.BODY_POSITION_SITTING_DOWN -> stringResource(R.string.sitting_down)
                            BodyPositions.BODY_POSITION_LYING_DOWN -> stringResource(R.string.lying_down)
                            BodyPositions.BODY_POSITION_RECLINING -> stringResource(R.string.reclining)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onOptionSelected(position)
                                onDismissRequest()
                            }
                            .padding(vertical = Spacings.small),
                        style = TextStyles.labelLarge
                    )
                }
            }
        })
}

@ThemePreviews
@Composable
fun BodyPositionsDialogPreview() {
    SpeziTheme(isPreview = true) {
        BodyPositionsDialog(
            onDismissRequest = {},
            onOptionSelected = {},
            bodyPositions = BodyPositions.entries
        )
    }
}
