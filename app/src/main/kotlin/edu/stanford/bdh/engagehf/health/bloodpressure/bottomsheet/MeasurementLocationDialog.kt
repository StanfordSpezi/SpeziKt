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
fun MeasurementLocationDialog(
    onDismissRequest: () -> Unit,
    onOptionSelected: (MeasurementLocations) -> Unit,
    measurementLocations: List<MeasurementLocations>,
) {
    BasicAlertDialog(
        modifier = Modifier
            .background(Colors.surface, shape = RoundedCornerShape(Spacings.medium))
            .padding(Spacings.medium),
        onDismissRequest = onDismissRequest,
        content = {
            Column {
                Text(
                    text = stringResource(R.string.measurement_location),
                    style = TextStyles.titleLarge,
                    color = Colors.onSurface
                )
                VerticalSpacer()
                measurementLocations.forEach { location ->
                    Text(
                        text = when (location) {
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
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onOptionSelected(location)
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
fun MeasurementLocationDialogPreview() {
    SpeziTheme(isPreview = true) {
        MeasurementLocationDialog(
            onDismissRequest = {},
            onOptionSelected = {},
            measurementLocations = MeasurementLocations.entries
        )
    }
}
