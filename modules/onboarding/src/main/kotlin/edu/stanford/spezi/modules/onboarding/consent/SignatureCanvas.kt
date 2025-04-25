@file:Suppress("MagicNumber")
package edu.stanford.spezi.modules.onboarding.consent

import android.graphics.Paint
import android.view.MotionEvent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp

@ExperimentalComposeUiApi
@Composable
internal fun SignatureCanvas(
    paths: MutableList<Path>,
    firstName: String,
    lastName: String,
    onPathAdd: (Path) -> Unit,
) {
    if (paths.isEmpty()) {
        paths.add(Path())
    }
    val currentPath = paths.last()
    val movePath = remember { mutableStateOf<Offset?>(null) }
    val canvasSize = remember { mutableStateOf(IntSize.Zero) }

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .background(Color.White)
            .onSizeChanged { size -> canvasSize.value = size }
            .pointerInteropFilter {
                when (it.action) {
                    MotionEvent.ACTION_DOWN -> {
                        if (it.x in 0f..canvasSize.value.width.toFloat() && it.y in 0f..canvasSize.value.height.toFloat()) {
                            currentPath.moveTo(it.x, it.y)
                        }
                    }

                    MotionEvent.ACTION_MOVE -> {
                        if (it.x in 0f..canvasSize.value.width.toFloat() && it.y in 0f..canvasSize.value.height.toFloat()) {
                            movePath.value = Offset(it.x, it.y)
                            onPathAdd(currentPath)
                        }
                    }

                    else -> {
                        movePath.value = null
                    }
                }
                true
            }
    ) {
        movePath.value?.let {
            currentPath.lineTo(it.x, it.y)
            drawPath(
                path = currentPath,
                color = Color.Black,
                style = Stroke(10f)
            )
        }

        paths.forEach {
            drawPath(
                path = it,
                color = Color.Black,
                style = Stroke(10f)
            )
        }

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

        drawLine(
            start = Offset(x = 10f, y = size.height - 50f),
            end = Offset(x = size.width - 10f, y = size.height - 50f),
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

@OptIn(ExperimentalComposeUiApi::class)
@Preview
@Composable
private fun SignatureCanvasPreview() {
    val paths = remember { mutableStateListOf(Path()) }
    val firstName = remember { mutableStateOf("First Name") }
    val lastName = remember { mutableStateOf("Last Name") }
    paths.add(Path())

    SignatureCanvas(
        paths = paths,
        firstName = firstName.value,
        lastName = lastName.value,
        onPathAdd = { paths.add(it) }
    )
}
