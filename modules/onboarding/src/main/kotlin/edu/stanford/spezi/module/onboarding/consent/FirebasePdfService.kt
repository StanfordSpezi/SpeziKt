package edu.stanford.spezi.module.onboarding.consent

import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import androidx.compose.ui.graphics.asAndroidPath
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import edu.stanford.spezi.core.design.component.markdown.MarkdownElement
import edu.stanford.spezi.core.design.component.markdown.parseMarkdown
import edu.stanford.spezi.core.logging.speziLogger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.time.LocalDate
import javax.inject.Inject

class FirebasePdfService @Inject internal constructor(
    private val firebaseStorage: FirebaseStorage,
    private val firebaseAuth: FirebaseAuth,
) : PdfService {
    private val logger by speziLogger()

    override suspend fun createPdf(uiState: ConsentUiState): Boolean {
        kotlin.runCatching {
            val pdfDocument = PdfDocument()
            val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
            val page = pdfDocument.startPage(pageInfo)

            val canvas = page.canvas

            var yOffset = 50f


            parseMarkdown(uiState.markdownText).forEach {
                when (it) {
                    is MarkdownElement.Heading -> {
                        val paint = Paint().apply {
                            color = android.graphics.Color.BLACK
                            textSize = when (it.level) {
                                1 -> 24f
                                2 -> 20f
                                else -> 16f
                            }
                            typeface = Typeface.DEFAULT_BOLD
                        }
                        canvas.drawText(it.text, 10f, yOffset, paint)
                        yOffset += paint.textSize + 10f
                    }

                    is MarkdownElement.Paragraph -> {
                        val paint = TextPaint().apply {
                            color = android.graphics.Color.BLACK
                            textSize = 14f
                        }
                        val layout = StaticLayout(
                            it.text,
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
                        yOffset += layout.height + 10f
                    }

                    is MarkdownElement.Bold -> {
                        val paint = Paint().apply {
                            color = android.graphics.Color.BLACK
                            textSize = 14f
                            typeface = Typeface.DEFAULT_BOLD
                        }
                        canvas.drawText(it.text, 10f, yOffset, paint)
                        yOffset += paint.textSize + 10f
                    }

                    is MarkdownElement.ListItem -> {
                        val paint = Paint().apply {
                            color = android.graphics.Color.BLACK
                            textSize = 14f
                        }
                        canvas.drawText("- ${it.text}", 10f, yOffset, paint)
                        yOffset += paint.textSize + 10f
                    }
                }
            }
            yOffset += 100f
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
            yOffset -= 80f
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
            pdfDocument.finishPage(page)

            withContext(Dispatchers.IO) {
                firebaseAuth.uid?.let { uid ->
                    val outputStream = ByteArrayOutputStream()
                    pdfDocument.writeTo(outputStream)
                    pdfDocument.close()
                    val byteArray = outputStream.toByteArray()
                    val inputStream = ByteArrayInputStream(byteArray)
                    logger.i { "Uploading file to Firebase Storage" }
                    val reference = firebaseStorage.getReference("users/$uid/signature.pdf")
                        .putStream(inputStream).result.metadata?.reference
                    reference?.downloadUrl?.await()
                }
            }
        }.onSuccess {
            return true
        }.onFailure {
            return false
        }
        return false
    }
}