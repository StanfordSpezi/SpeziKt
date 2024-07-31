package edu.stanford.bdh.engagehf.health.symptoms

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberAxisLabelComponent
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottomAxis
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStartAxis
import com.patrykandpatrick.vico.compose.cartesian.fullWidth
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLine
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberPoint
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoScrollState
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoZoomState
import com.patrykandpatrick.vico.compose.common.component.rememberShapeComponent
import com.patrykandpatrick.vico.compose.common.component.rememberTextComponent
import com.patrykandpatrick.vico.compose.common.of
import com.patrykandpatrick.vico.compose.common.rememberLegendItem
import com.patrykandpatrick.vico.compose.common.rememberVerticalLegend
import com.patrykandpatrick.vico.compose.common.shader.color
import com.patrykandpatrick.vico.compose.common.vicoTheme
import com.patrykandpatrick.vico.core.cartesian.CartesianDrawContext
import com.patrykandpatrick.vico.core.cartesian.CartesianMeasureContext
import com.patrykandpatrick.vico.core.cartesian.HorizontalLayout
import com.patrykandpatrick.vico.core.cartesian.Scroll
import com.patrykandpatrick.vico.core.cartesian.Zoom
import com.patrykandpatrick.vico.core.cartesian.axis.AxisPosition
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.data.AxisValueOverrider
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.core.cartesian.data.ChartValues
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.patrykandpatrick.vico.core.cartesian.layer.LineCartesianLayer
import com.patrykandpatrick.vico.core.cartesian.layer.LineCartesianLayer.PointProvider.Companion.single
import com.patrykandpatrick.vico.core.cartesian.marker.DefaultCartesianMarker
import com.patrykandpatrick.vico.core.common.Dimensions
import com.patrykandpatrick.vico.core.common.component.TextComponent
import com.patrykandpatrick.vico.core.common.shader.DynamicShader
import com.patrykandpatrick.vico.core.common.shape.Shape
import edu.stanford.bdh.engagehf.health.HealthTable
import edu.stanford.bdh.engagehf.health.TimeRange
import edu.stanford.spezi.core.design.theme.Colors
import edu.stanford.spezi.core.design.theme.Colors.primary
import edu.stanford.spezi.core.design.theme.TextStyles
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@Composable
fun SymptomsPage() {
    val viewModel = hiltViewModel<SymptomsViewModel>()
    val uiState by viewModel.uiState.collectAsState()
    SymptomsPage(
        uiState = uiState,
        onAction = viewModel::onAction,
    )
}

@Composable
fun SymptomsPage(
    uiState: SymptomsUiState,
    onAction: (SymptomsViewModel.Action) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        when (uiState) {
            is SymptomsUiState.Error -> {
                Text(
                    text = uiState.message,
                    style = TextStyles.headlineMedium
                )
            }

            SymptomsUiState.Loading -> {
                CircularProgressIndicator(color = primary)
            }

            is SymptomsUiState.Success -> {
                //HealthHeader(uiState.data.headerData, onAction)
                SymptomsChart(uiState.data)
                HealthTable(uiState.data.tableEntries, { })
            }
        }
    }
}

@Composable
fun SymptomsChart(
    uiState: SymptomsUiData,
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
                    chartColors().map { color ->
                        rememberLine(
                            shader = DynamicShader.color(color),
                            backgroundShader = DynamicShader.color(Color.Transparent),
                            pointProvider = single(
                                rememberPoint(
                                    shapeComponent,
                                    5.dp,
                                )
                            ),
                        )
                    }
                ),
                axisValueOverrider = AxisValueOverrider.fixed(
                    maxY = 100f,
                    minY = 0f,
                    minX = uiState.chartData.first().xValues.minOrNull() ?: 0f,
                    maxX = uiState.chartData.first().xValues.maxOrNull() ?: 0f,
                )
            ),
            startAxis = rememberStartAxis(
                titleComponent = rememberTextComponent(),
                label = rememberAxisLabelComponent(),
                guideline = null,
                valueFormatter = CartesianValueFormatter.yPercent(),
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
            decorations = emptyList(),
            horizontalLayout = HorizontalLayout.fullWidth(),
            legend = rememberLegend(),
        ),
        modelProducer = modelProducer,
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
private fun rememberLegend() =
    rememberVerticalLegend<CartesianMeasureContext, CartesianDrawContext>(
        items =
        chartColors().mapIndexed { index, chartColor ->
            rememberLegendItem(
                icon = rememberShapeComponent(chartColor, Shape.Pill),
                labelComponent = rememberTextComponent(vicoTheme.textColor),
                label = "stringResource(R.string.series_x, index + 1)",
            )
        },
        iconSize = 8.dp,
        iconPadding = 8.dp,
        spacing = 4.dp,
        padding = Dimensions.of(top = 8.dp),
    )

@Composable
private fun chartColors() = listOf(
    primary,
    Colors.secondary.copy(alpha = 0.2f),
    Colors.tertiary.copy(alpha = 0.2f),
)
