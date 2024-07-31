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
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberPoint
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoScrollState
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoZoomState
import com.patrykandpatrick.vico.compose.common.component.rememberLineComponent
import com.patrykandpatrick.vico.compose.common.component.rememberShapeComponent
import com.patrykandpatrick.vico.compose.common.component.rememberTextComponent
import com.patrykandpatrick.vico.compose.common.of
import com.patrykandpatrick.vico.compose.common.shader.color
import com.patrykandpatrick.vico.core.cartesian.HorizontalLayout
import com.patrykandpatrick.vico.core.cartesian.Scroll
import com.patrykandpatrick.vico.core.cartesian.Zoom
import com.patrykandpatrick.vico.core.cartesian.axis.AxisPosition
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.data.AxisValueOverrider
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.ChartValues
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.patrykandpatrick.vico.core.cartesian.decoration.HorizontalLine
import com.patrykandpatrick.vico.core.cartesian.layer.LineCartesianLayer
import com.patrykandpatrick.vico.core.cartesian.layer.LineCartesianLayer.PointProvider.Companion.single
import com.patrykandpatrick.vico.core.cartesian.marker.DefaultCartesianMarker
import com.patrykandpatrick.vico.core.common.Dimensions
import com.patrykandpatrick.vico.core.common.component.TextComponent
import com.patrykandpatrick.vico.core.common.shader.DynamicShader
import com.patrykandpatrick.vico.core.common.shape.Shape
import edu.stanford.bdh.engagehf.health.TimeRange
import edu.stanford.spezi.core.design.theme.Colors.primary
import edu.stanford.spezi.core.design.theme.Colors.secondary
import edu.stanford.spezi.core.design.theme.ThemePreviews
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@Composable
fun HealthChart(
    uiState: HealthUiData,
    modifier: Modifier = Modifier,
) {
    val modelProducer = remember { CartesianChartModelProducer() }

    LaunchedEffect(uiState.chartData.hashCode()) {
        withContext(Dispatchers.Default) {
            if (uiState.chartData.isEmpty()) return@withContext
            modelProducer.runTransaction {
                lineSeries {
                    uiState.chartData.forEach {
                        series(x = it.xValues, y = it.yValues)
                    }
                }
            }
        }
    }
    val shapeComponent = rememberShapeComponent(
        shape = Shape.Pill,
    )

    val valueFormatter: (Float, ChartValues, AxisPosition.Vertical?) -> CharSequence =
        { index, _, _ ->
            val epochSecond = (index * 60).toLong()
            val dateTime =
                ZonedDateTime.ofInstant(Instant.ofEpochSecond(epochSecond), ZoneOffset.UTC)
            when (uiState.selectedTimeRange) {
                TimeRange.WEEKLY -> {
                    dateTime.toLocalDate().format(DateTimeFormatter.ofPattern("MMM dd"))
                }

                TimeRange.MONTHLY -> {
                    dateTime.toLocalDate().format(DateTimeFormatter.ofPattern("MMM yy"))
                }

                TimeRange.DAILY -> {
                    dateTime.toLocalDate().format(DateTimeFormatter.ofPattern("MMM dd"))
                }
            }
        }

    val marker = remember {
        DefaultCartesianMarker(
            label = TextComponent(),
            labelPosition = DefaultCartesianMarker.LabelPosition.AroundPoint,
            indicator = shapeComponent,
            indicatorSizeDp = 5f,
        )
    }

    CartesianChartHost(
        chart =
        rememberCartesianChart(
            rememberLineCartesianLayer(
                LineCartesianLayer.LineProvider.series(
                    rememberLine(
                        shader = DynamicShader.color(primary),
                        backgroundShader = DynamicShader.color(Color.Transparent),
                        pointProvider = single(
                            rememberPoint(
                                shapeComponent,
                                5.dp,
                            )
                        ),
                    ),
                ),
                axisValueOverrider = AxisValueOverrider.adaptiveYValues(1.05f, true),
            ),
            startAxis = rememberStartAxis(
                title = "Weight in lbs",
                titleComponent = rememberTextComponent(),
                label = rememberAxisLabelComponent(),
                guideline = null,
            ),
            bottomAxis = rememberBottomAxis(
                guideline = null,
                valueFormatter = valueFormatter,
                itemPlacer = remember {
                    HorizontalAxis.ItemPlacer.default(
                        spacing = 2,
                        offset = 0,
                        addExtremeLabelPadding = false
                    )
                },
            ),
            marker = marker,
            decorations = uiState.averageData?.let { averageWeight ->
                listOf(rememberComposeHorizontalLine(averageWeight))
            }
                ?: emptyList(),
            horizontalLayout = HorizontalLayout.fullWidth(),
        ),
        modelProducer = modelProducer,
        modifier = modifier,
        zoomState = rememberVicoZoomState(
            zoomEnabled = false,
            initialZoom = remember { Zoom.max(Zoom.static(), Zoom.Content) },
        ),
        scrollState = rememberVicoScrollState(
            initialScroll = Scroll.Absolute.End,
        ),
    )
}

@Composable
private fun rememberComposeHorizontalLine(averageWeight: AverageHealthData): HorizontalLine {
    return rememberHorizontalLine(
        y = { averageWeight.value },
        line = rememberLineComponent(secondary, 2.dp),
        labelComponent =
        rememberTextComponent(
            margins = Dimensions.of(4.dp),
            padding =
            Dimensions.of(
                8.dp,
                2.dp,
            ),
            background = rememberShapeComponent(secondary, Shape.Pill),
        ),
        label = { averageWeight.formattedValue },
    )
}

@ThemePreviews
@Composable
fun WeightChartPreview() {
}
