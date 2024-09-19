package edu.stanford.bdh.engagehf.bluetooth.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import edu.stanford.bdh.engagehf.bluetooth.component.VitalDisplayDataFactory.createVitalDisplayState
import edu.stanford.bdh.engagehf.bluetooth.data.models.VitalDisplayData
import edu.stanford.spezi.core.design.component.DefaultElevatedCard
import edu.stanford.spezi.core.design.component.RectangleShimmerEffect
import edu.stanford.spezi.core.design.component.height
import edu.stanford.spezi.core.design.theme.Colors
import edu.stanford.spezi.core.design.theme.Spacings
import edu.stanford.spezi.core.design.theme.SpeziTheme
import edu.stanford.spezi.core.design.theme.TextStyles
import edu.stanford.spezi.core.design.theme.ThemePreviews
import edu.stanford.spezi.core.utils.extensions.testIdentifier
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun VitalDisplay(
    modifier: Modifier = Modifier,
    vitalDisplayUiState: VitalDisplayData,
) {
    DefaultElevatedCard(
        modifier = modifier
            .height(130.dp)
            .fillMaxSize()
            .testIdentifier(WeightDisplayTestIdentifier.ROOT)
    ) {
        Column(
            modifier = Modifier
                .padding(Spacings.small)
                .fillMaxSize()
        ) {
            Text(
                text = vitalDisplayUiState.title,
                style = TextStyles.titleMedium,
                color = Colors.onBackground,
                modifier = Modifier.testIdentifier(WeightDisplayTestIdentifier.TITLE),
            )
            when (vitalDisplayUiState.status) {
                OperationStatus.SUCCESS -> {
                    Text(
                        text = "${vitalDisplayUiState.value} ${vitalDisplayUiState.unit}",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Colors.primary,
                        modifier = Modifier
                            .padding(vertical = Spacings.small)
                            .testIdentifier(WeightDisplayTestIdentifier.VALUE),
                    )
                    Text(
                        text = "${vitalDisplayUiState.date}",
                        style = TextStyles.bodySmall,
                        color = Colors.onBackground,
                        modifier = Modifier
                            .padding(top = Spacings.small)
                            .testIdentifier(WeightDisplayTestIdentifier.DATE),
                    )
                }

                OperationStatus.FAILURE -> {
                    Text(
                        text = "Error: ${vitalDisplayUiState.error}",
                        style = TextStyles.bodySmall,
                        color = Colors.error,
                        modifier = Modifier
                            .padding(top = Spacings.small)
                            .testIdentifier(WeightDisplayTestIdentifier.ERROR),
                    )
                }

                OperationStatus.PENDING -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(vertical = Spacings.medium)
                            .testIdentifier(WeightDisplayTestIdentifier.PENDING),
                        verticalArrangement = Arrangement.spacedBy(Spacings.medium)
                    ) {
                        RectangleShimmerEffect(
                            modifier = Modifier
                                .fillMaxWidth(fraction = 0.8f)
                                .height(textStyle = TextStyles.headlineMedium)
                        )
                        RectangleShimmerEffect(
                            modifier = Modifier
                                .fillMaxWidth(fraction = 0.5f)
                                .height(textStyle = TextStyles.bodySmall)
                        )
                    }
                }

                OperationStatus.NO_DATA -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = Spacings.small)
                            .testIdentifier(WeightDisplayTestIdentifier.PENDING),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No data available",
                            style = TextStyles.bodyMedium
                        )
                    }
                }
            }
        }
    }
}

enum class WeightDisplayTestIdentifier {
    ROOT,
    TITLE,
    VALUE,
    DATE,
    ERROR,
    PENDING,
}

enum class OperationStatus {
    FAILURE,
    SUCCESS,
    PENDING,
    NO_DATA,
}

private object VitalDisplayDataFactory {
    fun createVitalDisplayState(): VitalDisplayData {
        val title = "Weight"
        val value = "70.0"
        val unit = "kg"
        val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        val date = dateFormat.format(Date())
        return VitalDisplayData(title, value, unit, date)
    }
}

private class VitalDisplayDataProvider : PreviewParameterProvider<VitalDisplayData> {
    override val values: Sequence<VitalDisplayData> = sequenceOf(
        createVitalDisplayState(),
        createVitalDisplayState().copy(
            title = "Blood Pressure",
            value = "120/80",
            unit = "mmHg",
            status = OperationStatus.SUCCESS
        ),
        createVitalDisplayState().copy(
            title = "Heart Rate", value = "70", unit = "bpm",
            status = OperationStatus.FAILURE, error = "Cannot retrieve data"
        ),
        createVitalDisplayState().copy(
            title = "Heart Rate", status = OperationStatus.NO_DATA
        )
    )
}

@ThemePreviews
@Composable
@Suppress("UnusedPrivateMember")
private fun VitalDisplayPreview(@PreviewParameter(VitalDisplayDataProvider::class) state: VitalDisplayData) {
    SpeziTheme(isPreview = true) {
        VitalDisplay(
            vitalDisplayUiState = state
        )
    }
}
