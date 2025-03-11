package edu.stanford.bdh.engagehf.health.bloodpressure.bottomsheet

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import edu.stanford.bdh.engagehf.R
import edu.stanford.bdh.engagehf.health.components.ItemsDialog
import edu.stanford.spezi.ui.SpeziTheme
import edu.stanford.spezi.ui.ThemePreviews

@Composable
fun BodyPositionsDialog(
    onDismissRequest: () -> Unit,
    onOptionSelected: (BodyPositions) -> Unit,
    bodyPositions: List<BodyPositions>,
) {
    val items = bodyPositions.map {
        when (it) {
            BodyPositions.BODY_POSITION_UNKNOWN -> stringResource(R.string.not_set)
            BodyPositions.BODY_POSITION_STANDING_UP -> stringResource(R.string.standing_up)
            BodyPositions.BODY_POSITION_SITTING_DOWN -> stringResource(R.string.sitting_down)
            BodyPositions.BODY_POSITION_LYING_DOWN -> stringResource(R.string.lying_down)
            BodyPositions.BODY_POSITION_RECLINING -> stringResource(R.string.reclining)
        }
    }
    ItemsDialog(
        title = stringResource(id = R.string.body_position),
        items = items,
        onDismissRequest = onDismissRequest,
        onOptionSelected = { onOptionSelected(bodyPositions[it]) }
    )
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
