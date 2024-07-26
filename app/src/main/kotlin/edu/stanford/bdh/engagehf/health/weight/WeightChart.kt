package edu.stanford.bdh.engagehf.health.weight

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberAxisLabelComponent
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottomAxis
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStartAxis
import com.patrykandpatrick.vico.compose.cartesian.decoration.rememberHorizontalLine
import com.patrykandpatrick.vico.compose.cartesian.fullWidth
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLine
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoZoomState
import com.patrykandpatrick.vico.compose.common.component.rememberLineComponent
import com.patrykandpatrick.vico.compose.common.component.rememberShapeComponent
import com.patrykandpatrick.vico.compose.common.component.rememberTextComponent
import com.patrykandpatrick.vico.compose.common.of
import com.patrykandpatrick.vico.compose.common.shader.color
import com.patrykandpatrick.vico.core.cartesian.CartesianChart
import com.patrykandpatrick.vico.core.cartesian.HorizontalLayout
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.patrykandpatrick.vico.core.cartesian.decoration.HorizontalLine
import com.patrykandpatrick.vico.core.cartesian.layer.LineCartesianLayer
import com.patrykandpatrick.vico.core.cartesian.marker.DefaultCartesianMarker
import com.patrykandpatrick.vico.core.common.Dimensions
import com.patrykandpatrick.vico.core.common.component.TextComponent
import com.patrykandpatrick.vico.core.common.data.ExtraStore
import com.patrykandpatrick.vico.core.common.shader.DynamicShader
import com.patrykandpatrick.vico.core.common.shape.Shape
import edu.stanford.spezi.core.design.theme.Colors.primary
import edu.stanford.spezi.core.design.theme.ThemePreviews
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.DateFormatSymbols
import java.time.ZonedDateTime
import java.util.Locale

@Composable
fun WeightChart(
    uiState: WeightUiData,
    modifier: Modifier = Modifier,
) {
    val modelProducer = remember { CartesianChartModelProducer() }

    LaunchedEffect(uiState.chartWeights.hashCode()) {
        withContext(Dispatchers.Default) {
            if (uiState.chartWeights.isEmpty()) return@withContext
            modelProducer.runTransaction {
                val xValue = uiState.chartWeights.map { it.xAxis }
                val yValue =
                    uiState.chartWeights.map { it.value }
                lineSeries { series(x = xValue, y = yValue) }
            }
        }
    }

    val marker = remember {
        DefaultCartesianMarker(
            label = TextComponent(),
            indicatorSizeDp = 20F,
        )
    }

    /*    val valueFormatter: (Float, ChartValues, AxisPosition.Vertical?) -> CharSequence =
            { index, _, _ ->
                if (uiState.selectedTimeRange == TimeRange.MONTHLY) {
                    uiState.healthRecords.sortedBy { it.zonedDateTime }.reversed()
                        .getOrNull(index.toInt())
                        ?.formattedDate
                        ?: ""
                } else {
                    uiState.healthRecords.sortedBy { it.zonedDateTime }.reversed()
                        .getOrNull(index.toInt())
                        ?.formattedDateAndTime
                        ?: ""
                }
            }
        val xValues = uiState.filteredRecords.map { it.zonedDateTime.monthValue.toFloat() }*/

    CartesianChartHost(
        chart =
        rememberCartesianChart(
            rememberLineCartesianLayer(
                LineCartesianLayer.LineProvider.series(
                    rememberLine(DynamicShader.color(primary)),
                )
            ),
            startAxis = rememberStartAxis(
                title = "Weight in kg",
                label = rememberAxisLabelComponent(),
            ),
            bottomAxis = rememberBottomAxis(
                guideline = null,
                valueFormatter = bottomAxisValueFormatter,
                title = "Date",
                itemPlacer = remember {
                    HorizontalAxis.ItemPlacer.default(spacing = 3, addExtremeLabelPadding = true)
                }
            ),
            marker = marker,
            decorations = listOf(rememberComposeHorizontalLine()),
            horizontalLayout = HorizontalLayout.fullWidth(),
            // persistentMarkers = createPersistentMarkerLambda(xValues, marker),
        ),
        modelProducer = modelProducer,
        modifier = modifier,
        zoomState = rememberVicoZoomState(zoomEnabled = false),
    )
}

@Composable
private fun rememberComposeHorizontalLine(): HorizontalLine {
    val color = Color(HORIZONTAL_LINE_COLOR)
    return rememberHorizontalLine(
        y = { HORIZONTAL_LINE_Y.toFloat() },
        line = rememberLineComponent(color, HORIZONTAL_LINE_THICKNESS_DP.dp),
        labelComponent =
        rememberTextComponent(
            margins = Dimensions.of(HORIZONTAL_LINE_LABEL_MARGIN_DP.dp),
            padding =
            Dimensions.of(
                HORIZONTAL_LINE_LABEL_HORIZONTAL_PADDING_DP.dp,
                HORIZONTAL_LINE_LABEL_VERTICAL_PADDING_DP.dp,
            ),
            background = rememberShapeComponent(color, Shape.Pill),
        ),
        label = { "Average 83" }
    )
}

private const val HORIZONTAL_LINE_Y = 83.0
private const val HORIZONTAL_LINE_COLOR = -2893786
private const val HORIZONTAL_LINE_THICKNESS_DP = 2f
private const val HORIZONTAL_LINE_LABEL_HORIZONTAL_PADDING_DP = 8f
private const val HORIZONTAL_LINE_LABEL_VERTICAL_PADDING_DP = 2f
private const val HORIZONTAL_LINE_LABEL_MARGIN_DP = 4f

private const val PERSISTENT_MARKER_X = 7f

private val monthNames = DateFormatSymbols.getInstance(Locale.US).shortMonths

private val baseYear = 2024
private val bottomAxisValueFormatter = CartesianValueFormatter { x, _, _ ->
    val yearOffset = x.toInt() / 12
    val year = baseYear + yearOffset
    val monthIndex = x.toInt() % 12
    "${monthNames[monthIndex]} ’${year.toString().substring(2)}"
}

fun createPersistentMarkerLambda(
    xValues: List<Float>,
    marker: DefaultCartesianMarker,
): (CartesianChart.PersistentMarkerScope.(ExtraStore) -> Unit) {
    return {
        xValues.forEach { x ->
            marker at x
        }
    }
}

@ThemePreviews
@Composable
fun WeightChartPreview() {
    WeightChart(
        uiState = WeightUiData(
            chartWeights = listOf(
                WeightData(
                    id = "1",
                    value = 80f,
                    formattedValue = "80",
                    date = ZonedDateTime.now(),
                    formattedDate = "2024-01-01",
                    xAxis = 0f,
                    trend = 0f,
                    formattedTrend = "0",
                ),
            )
        )
    )
}
