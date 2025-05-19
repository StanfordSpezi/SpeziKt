package edu.stanford.bdh.engagehf.bluetooth.component

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import edu.stanford.bdh.engagehf.R
import edu.stanford.spezi.modules.design.component.VerticalSpacer
import edu.stanford.spezi.ui.testIdentifier
import edu.stanford.spezi.ui.theme.Colors
import edu.stanford.spezi.ui.theme.Sizes
import edu.stanford.spezi.ui.theme.Spacings
import edu.stanford.spezi.ui.theme.SpeziTheme
import edu.stanford.spezi.ui.theme.TextStyles
import edu.stanford.spezi.ui.theme.ThemePreviews

@Composable
fun DoNewMeasurementBottomSheet() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(Spacings.medium),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(
                id = R.string.new_measurement
            ),
            style = TextStyles.titleLarge,
            modifier = Modifier.testIdentifier(DoNewMeasurementBottomSheetTestIdentifier.TITLE),
        )
        VerticalSpacer()
        CircularProgressIndicator(
            modifier = Modifier.testIdentifier(DoNewMeasurementBottomSheetTestIdentifier.PROGRESS_BAR)
        )
        VerticalSpacer()
        Text(
            text = stringResource(R.string.new_measurement_text),
            style = TextStyles.bodyMedium,
            modifier = Modifier.testIdentifier(DoNewMeasurementBottomSheetTestIdentifier.DESCRIPTION),
        )
        VerticalSpacer()
        Row {
            val infiniteTransition = rememberInfiniteTransition(label = "")
            val color by infiniteTransition.animateColor(
                initialValue = Colors.primary,
                targetValue = Color.Transparent,
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = 1000, easing = LinearEasing),
                    repeatMode = RepeatMode.Reverse
                ), label = ""
            )

            Icon(
                painter = painterResource(id = edu.stanford.spezi.modules.design.R.drawable.ic_blood_pressure),
                contentDescription = stringResource(R.string.blood_pressure_icon_content_description),
                modifier = Modifier
                    .size(Sizes.Icon.large)
                    .testIdentifier(DoNewMeasurementBottomSheetTestIdentifier.BLOOD_PRESSURE_ICON),
                tint = color
            )
            Spacer(modifier = Modifier.width(Spacings.medium))
            Icon(
                painter = painterResource(id = edu.stanford.spezi.modules.design.R.drawable.ic_monitor_weight),
                contentDescription = stringResource(R.string.info_icon_content_description),
                modifier = Modifier
                    .size(Sizes.Icon.large)
                    .testIdentifier(DoNewMeasurementBottomSheetTestIdentifier.WEIGHT_ICON),
                tint = color
            )
        }
        VerticalSpacer(height = Spacings.large)
    }
}

enum class DoNewMeasurementBottomSheetTestIdentifier {
    TITLE,
    DESCRIPTION,
    PROGRESS_BAR,
    BLOOD_PRESSURE_ICON,
    WEIGHT_ICON,
}

@ThemePreviews
@Composable
fun PreviewDoNewMeasurementBottomSheetContent() {
    SpeziTheme {
        DoNewMeasurementBottomSheet()
    }
}
