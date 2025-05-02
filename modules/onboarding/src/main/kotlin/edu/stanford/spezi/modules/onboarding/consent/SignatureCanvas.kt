@file:Suppress("MagicNumber")
package edu.stanford.spezi.modules.onboarding.consent

import android.graphics.Paint
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.ink.authoring.InProgressStrokeId
import androidx.ink.authoring.InProgressStrokesFinishedListener
import androidx.ink.authoring.InProgressStrokesView
import androidx.ink.brush.Brush
import androidx.ink.brush.StockBrushes
import androidx.ink.strokes.Stroke
import androidx.input.motionprediction.MotionEventPredictor


@ExperimentalComposeUiApi
@Composable
internal fun SignatureCanvas(
    firstName: String,
    lastName: String,
    onPathAdd: (Map<InProgressStrokeId, Stroke>) -> Unit,
    onViewCreated: (InProgressStrokesView) -> Unit = {}
) {

    val currentPointerId = remember { mutableStateOf<Int?>(null) }
    val currentStrokeId = remember { mutableStateOf<InProgressStrokeId?>(null) }

    val defaultBrush = Brush.createWithColorIntArgb(
        family = StockBrushes.pressurePenLatest,
        colorIntArgb = Color.Black.toArgb(),
        size = 5F,
        epsilon = 0.1F
    )

    val finishedListener = object : InProgressStrokesFinishedListener {
        override fun onStrokesFinished(strokes: Map<InProgressStrokeId, Stroke>) {
            onPathAdd(strokes)
        }
    }

    Box(modifier = Modifier
        .fillMaxWidth()
        .background(Color.White)
        .height(150.dp),) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                // Create a new FrameLayout that will be our root view
                val rootView = FrameLayout(ctx)

                // Create a new InProgressStrokesView instance every time
                val inProgressStrokesView = InProgressStrokesView(ctx).apply {
                    layoutParams = FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT,
                    )
                }

                onViewCreated(inProgressStrokesView)

                inProgressStrokesView.addFinishedStrokesListener(finishedListener)

                val predictor = MotionEventPredictor.newInstance(rootView)
                val touchListener = View.OnTouchListener { view, event ->
                    predictor.record(event)
                    val predictedEvent = predictor.predict()

                    try {
                        when (event.actionMasked) {
                            MotionEvent.ACTION_DOWN -> {
                                // First pointer - treat it as inking.
                                view.requestUnbufferedDispatch(event)
                                val pointerIndex = event.actionIndex
                                val pointerId = event.getPointerId(pointerIndex)
                                currentPointerId.value = pointerId
                                currentStrokeId.value =
                                    inProgressStrokesView.startStroke(
                                        event = event,
                                        pointerId = pointerId,
                                        brush = defaultBrush
                                    )
                                true
                            }

                            MotionEvent.ACTION_MOVE -> {
                                val pointerId = checkNotNull(currentPointerId.value)
                                val strokeId = checkNotNull(currentStrokeId.value)

                                for (pointerIndex in 0 until event.pointerCount) {
                                    if (event.getPointerId(pointerIndex) != pointerId) continue
                                    inProgressStrokesView.addToStroke(
                                        event,
                                        pointerId,
                                        strokeId,
                                        predictedEvent
                                    )
                                }
                                true
                            }

                            MotionEvent.ACTION_UP -> {
                                val pointerIndex = event.actionIndex
                                val pointerId = event.getPointerId(pointerIndex)
                                val strokeId = checkNotNull(currentStrokeId.value)
                                inProgressStrokesView.finishStroke(
                                    event,
                                    pointerId,
                                    strokeId
                                )
                                view.performClick()
                                true
                            }

                            MotionEvent.ACTION_CANCEL -> {
                                val currentStrokeId = checkNotNull(currentStrokeId.value)
                                inProgressStrokesView.cancelStroke(currentStrokeId, event)
                                true
                            }

                            else -> false
                        }
                    } finally {
                        predictedEvent?.recycle()
                    }
                }

                rootView.setOnTouchListener(touchListener)
                rootView.addView(inProgressStrokesView)

                rootView
            },
        )

        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            val lineY = size.height - 50f
            drawLine(
                start = Offset(x = 10f, y = lineY),
                end = Offset(x = size.width - 10f, y = lineY),
                color = Color.Gray,
                strokeWidth = 2f
            )
            drawLine(
            start = Offset(x = 10f, y = size.height - 55f),
            end = Offset(x = 60f, y = size.height - 105f),
            color = Color.Gray,
            strokeWidth = 2f
            )
            drawLine(
                start = Offset(x = 60f, y = size.height - 55f),
                end = Offset(x = 10f, y = size.height - 105f),
                color = Color.Gray,
                strokeWidth = 2f
            )

            drawIntoCanvas { canvas ->
                val paint = Paint().apply {
                    color = android.graphics.Color.GRAY
                    textSize = 30f
                }
                canvas.nativeCanvas.drawText(
                    "$firstName $lastName",
                    10f,
                    size.height - 10f,
                    paint
                )
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Preview
@Composable
private fun SignatureCanvasPreview() {
    val firstName = remember { mutableStateOf("First Name") }
    val lastName = remember { mutableStateOf("Last Name") }

    SignatureCanvas(
        firstName = firstName.value,
        lastName = lastName.value,
        onPathAdd = {},
        onViewCreated = {}
    )
}
