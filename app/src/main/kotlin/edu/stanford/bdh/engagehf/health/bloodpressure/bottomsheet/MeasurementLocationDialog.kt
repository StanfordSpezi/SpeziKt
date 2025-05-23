package edu.stanford.bdh.engagehf.health.bloodpressure.bottomsheet

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import edu.stanford.bdh.engagehf.R
import edu.stanford.bdh.engagehf.health.components.ItemsDialog
import edu.stanford.spezi.ui.theme.SpeziTheme
import edu.stanford.spezi.ui.theme.ThemePreviews

@Composable
fun MeasurementLocationDialog(
    onDismissRequest: () -> Unit,
    onOptionSelected: (MeasurementLocations) -> Unit,
    measurementLocations: List<MeasurementLocations>,
) {
    val items = measurementLocations.map {
        when (it) {
            MeasurementLocations.MEASUREMENT_LOCATION_UNKNOWN -> stringResource(R.string.not_set)
            MeasurementLocations.MEASUREMENT_LOCATION_LEFT_WRIST -> stringResource(R.string.left_wrist)
            MeasurementLocations.MEASUREMENT_LOCATION_RIGHT_WRIST -> stringResource(
                R.string.right_wrist
            )

            MeasurementLocations.MEASUREMENT_LOCATION_LEFT_UPPER_ARM -> stringResource(
                R.string.left_upper_arm
            )

            MeasurementLocations.MEASUREMENT_LOCATION_RIGHT_UPPER_ARM -> stringResource(
                R.string.right_upper_arm
            )
        }
    }
    ItemsDialog(
        title = stringResource(id = R.string.measurement_location),
        items = items,
        onDismissRequest = onDismissRequest,
        onOptionSelected = { onOptionSelected(measurementLocations[it]) }
    )
}

@ThemePreviews
@Composable
fun MeasurementLocationDialogPreview() {
    SpeziTheme {
        MeasurementLocationDialog(
            onDismissRequest = {},
            onOptionSelected = {},
            measurementLocations = MeasurementLocations.entries
        )
    }
}
