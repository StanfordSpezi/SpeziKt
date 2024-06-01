package edu.stanford.spezi.module.onboarding.consent

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.unit.IntSize
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.stanford.spezi.core.logging.speziLogger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

@HiltViewModel
class ConsentViewModel @Inject internal constructor(
    private val firebaseStorage: FirebaseStorage,
    private val firebaseAuth: FirebaseAuth,
) : ViewModel() {
    private val logger by speziLogger()

    private val _uiState = MutableStateFlow(ConsentUiState())
    val uiState: StateFlow<ConsentUiState> = _uiState

    fun onAction(action: ConsentAction) {
        _uiState.value = when (action) {
            is ConsentAction.TextFieldUpdate -> {
                when (action.type) {
                    TextFieldType.FIRST_NAME -> {
                        _uiState.value.copy(firstName = FieldState(value = action.newValue)) // TODO do error validation
                    }

                    TextFieldType.LAST_NAME -> {
                        _uiState.value.copy(lastName = FieldState(value = action.newValue)) // TODO do error validation
                    }
                }
            }

            is ConsentAction.AddPath -> {
                _uiState.value.copy(paths = _uiState.value.paths + action.path)
            }

            is ConsentAction.Undo -> {
                _uiState.value.copy(paths = _uiState.value.paths.dropLast(1))
            }

            is ConsentAction.Consent -> {
                viewModelScope.launch {
                    createPdf()
                }
                _uiState.value
            }
        }
    }

    private suspend fun createPdf(
    ) {
        try {
            val pdfDocument = PdfDocument()
            val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
            val page = pdfDocument.startPage(pageInfo)

            val canvas = page.canvas

            val paintHeader = Paint().apply {
                color = android.graphics.Color.BLACK
                textSize = 20f
            }
            canvas.drawText("First Name: ${uiState.value.firstName.value}", 10f, 100f, paintHeader)
            canvas.drawText("Last Name: ${uiState.value.lastName.value}", 10f, 150f, paintHeader)

            val paintSignature = Paint().apply {
                color = android.graphics.Color.BLACK
                strokeWidth = 3f
                style = Paint.Style.STROKE
            }

            val scaleFactor = 0.2f
            canvas.save()
            canvas.scale(scaleFactor, scaleFactor)

            uiState.value.paths.forEach { path ->
                canvas.drawPath(path.asAndroidPath(), paintSignature)
            }
            canvas.restore()
            pdfDocument.finishPage(page)

            withContext(Dispatchers.IO) {
                firebaseAuth.uid?.let { uid ->
                    kotlin.runCatching {
                        val outputStream = ByteArrayOutputStream()
                        pdfDocument.writeTo(outputStream)
                        pdfDocument.close()
                        val byteArray = outputStream.toByteArray()
                        val inputStream = ByteArrayInputStream(byteArray)

                        logger.i { "Uploading file to Firebase Storage" }
                        val reference = firebaseStorage.getReference("users/$uid/signature.pdf")
                            .putStream(inputStream).result.metadata?.reference
                        reference?.downloadUrl?.await()
                    }.onFailure {
                        logger.e { it.message.toString() }
                    }.onSuccess {
                        logger.i { "Download URL: $it" }
                    }
                }
            }
        } catch (e: Exception) {
            logger.e { "Create PDF Failed: " + e.message.toString() }
        }
    }

    // TODO option two: make a bitmap out of the screen and save it to pdf; otherwise we rebuild the screen in the pdf
    fun bitmapToPdf(bitmap: Bitmap, file: File) {
        val document = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(bitmap.width, bitmap.height, 1).create()
        val page = document.startPage(pageInfo)
        val canvas = page.canvas
        canvas.drawBitmap(bitmap, 0f, 0f, null)
        document.finishPage(page)

        FileOutputStream(file).use {
            document.writeTo(it)
        }
        document.close()
    }

    fun captureComposableAsBitmap(
        composeView: ComposeView,
        composable: @Composable () -> Unit,
        size: IntSize
    ): Bitmap {
        val handler = Handler(Looper.getMainLooper())
        var bitmap: Bitmap? = null

        handler.post {
            composeView.apply {
                setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnDetachedFromWindow)
                setContent {
                    composable()
                }
                measure(
                    View.MeasureSpec.makeMeasureSpec(size.width, View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.makeMeasureSpec(size.height, View.MeasureSpec.EXACTLY)
                )
                layout(0, 0, size.width, size.height)
                bitmap = Bitmap.createBitmap(size.width, size.height, Bitmap.Config.ARGB_8888)
                val canvas = Canvas(bitmap!!)
                draw(canvas)
            }
        }

        while (bitmap == null) {
            Thread.sleep(10)
        }

        return bitmap!!
    }
}
