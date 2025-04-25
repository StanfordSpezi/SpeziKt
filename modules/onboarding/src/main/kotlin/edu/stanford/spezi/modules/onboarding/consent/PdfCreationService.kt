@file:Suppress("MagicNumber")
package edu.stanford.spezi.modules.onboarding.consent

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import androidx.compose.ui.graphics.asAndroidPath
import edu.stanford.spezi.core.coroutines.Dispatching
import edu.stanford.spezi.ui.markdown.MarkdownElement
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.time.LocalDate
import javax.inject.Inject

internal class PdfCreationService @Inject internal constructor(
    @Dispatching.IO private val ioCoroutineDispatcher: CoroutineDispatcher,
) {

    suspend fun createPdf(uiState: ConsentUiState): ByteArray = withContext(ioCoroutineDispatcher) {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        val page = pdfDocument.startPage(pageInfo)

        val canvas = page.canvas

        var yOffset = 50f

        uiState.markdownElements.forEach {
            yOffset = when (it) {
                is MarkdownElement.Heading -> drawHeading(canvas, it, yOffset)
                is MarkdownElement.Paragraph -> drawParagraph(canvas, it, yOffset)
                is MarkdownElement.Bold -> drawBold(canvas, it, yOffset)
                is MarkdownElement.ListItem -> drawListItem(canvas, it, yOffset)
            }
        }
        yOffset += 50f
        yOffset = drawNamesAndSignature(canvas, uiState, yOffset)

        pdfDocument.finishPage(page)

        val outputStream = ByteArrayOutputStream()
        pdfDocument.writeTo(outputStream)
        pdfDocument.close()
        outputStream.toByteArray()
    }

    private fun drawNamesAndSignature(
        canvas: Canvas,
        uiState: ConsentUiState,
        yOffset: Float,
    ): Float {
        val paintNames = Paint().apply {
            color = android.graphics.Color.BLACK
            textSize = 14f
        }
        canvas.drawText(
            "First Name: ${uiState.firstName.value} Last Name: ${uiState.lastName.value} Date: ${LocalDate.now()}",
            10f,
            yOffset,
            paintNames
        )
        val paintSignature = Paint().apply {
            color = android.graphics.Color.BLUE
            strokeWidth = 3f
            style = Paint.Style.STROKE
        }

        val scaleFactor = 0.2f
        canvas.save()
        canvas.scale(scaleFactor, scaleFactor)

        uiState.paths.forEach { path ->
            val androidPath = path.asAndroidPath()
            val offsetPath = android.graphics.Path(androidPath)
            offsetPath.offset(0f, yOffset * 5)
            canvas.drawPath(offsetPath, paintSignature)
        }
        canvas.restore()
        return yOffset - 80f
    }

    private fun drawHeading(
        canvas: Canvas,
        element: MarkdownElement.Heading,
        yOffset: Float,
    ): Float {
        val paint = Paint().apply {
            color = android.graphics.Color.BLACK
            textSize = when (element.level) {
                1 -> 24f
                2 -> 20f
                else -> 16f
            }
            typeface = Typeface.DEFAULT_BOLD
        }
        canvas.drawText(element.text, 10f, yOffset, paint)
        return yOffset + paint.textSize + 10f
    }

    private fun drawParagraph(
        canvas: Canvas,
        element: MarkdownElement.Paragraph,
        yOffset: Float,
    ): Float {
        val paint = TextPaint().apply {
            color = android.graphics.Color.BLACK
            textSize = 14f
        }
        val layout = StaticLayout(
            element.text,
            paint,
            canvas.width,
            Layout.Alignment.ALIGN_NORMAL,
            1f,
            0f,
            false
        )
        canvas.save()
        canvas.translate(10f, yOffset)
        layout.draw(canvas)
        canvas.restore()
        return yOffset + layout.height + 10f
    }

    private fun drawBold(canvas: Canvas, element: MarkdownElement.Bold, yOffset: Float): Float {
        val paint = Paint().apply {
            color = android.graphics.Color.BLACK
            textSize = 14f
            typeface = Typeface.DEFAULT_BOLD
        }
        canvas.drawText(element.text, 10f, yOffset, paint)
        return yOffset + paint.textSize + 10f
    }

    private fun drawListItem(
        canvas: Canvas,
        element: MarkdownElement.ListItem,
        yOffset: Float,
    ): Float {
        val paint = Paint().apply {
            color = android.graphics.Color.BLACK
            textSize = 14f
        }
        canvas.drawText("- ${element.text}", 10f, yOffset, paint)
        return yOffset + paint.textSize + 10f
    }
}
