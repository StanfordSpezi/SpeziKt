package edu.stanford.bdh.engagehf.messages

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import edu.stanford.bdh.engagehf.R
import edu.stanford.spezi.core.utils.MessageNotifier
import edu.stanford.spezi.spezi.core.logging.coroutines.di.Dispatching
import edu.stanford.spezi.spezi.core.logging.speziLogger
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

class HealthSummaryService @Inject constructor(
    private val healthSummaryRepository: HealthSummaryRepository,
    private val messageNotifier: MessageNotifier,
    @Dispatching.IO private val ioDispatcher: CoroutineDispatcher,
    @ApplicationContext private val context: Context,
) {
    private val logger by speziLogger()

    suspend fun generateHealthSummaryPdf(): Result<Unit> = withContext(ioDispatcher) {
        healthSummaryRepository.getHealthSummary()
            .mapCatching {
                val savePdfToFile = savePdfToFile(it)
                val pdfUri = FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.provider",
                    savePdfToFile
                )
                val pdfIntent = Intent(Intent.ACTION_VIEW).apply {
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    setDataAndType(pdfUri, MIME_TYPE_PDF)
                }
                if (pdfIntent.resolveActivity(context.packageManager) == null) {
                    messageNotifier.notify(messageId = R.string.no_pdf_reader_app_installed_error_message)
                } else {
                    context.startActivity(pdfIntent)
                }
            }.onFailure {
                messageNotifier.notify(messageId = R.string.health_summary_generate_error_message)
            }
    }

    private fun savePdfToFile(pdfBytes: ByteArray): File {
        logger.i { "PDF size: ${pdfBytes.size}" }

        val storageDir = context.getExternalFilesDir(null) ?: context.filesDir
        if (!storageDir.exists()) {
            storageDir.mkdirs()
        }
        val pdfFile = File(storageDir, FILE_NAME)
        FileOutputStream(pdfFile).use { fos ->
            fos.write(pdfBytes)
        }
        logger.i { "PDF saved to file: ${pdfFile.absolutePath}" }
        return pdfFile
    }

    companion object {
        const val FILE_NAME = "engage_hf_health_summary.pdf"
        const val MIME_TYPE_PDF = "application/pdf"
    }
}
