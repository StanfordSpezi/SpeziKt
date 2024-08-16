package edu.stanford.bdh.engagehf.messages

import android.content.Context
import android.content.Intent
import android.os.Environment
import androidx.core.content.FileProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import edu.stanford.spezi.core.logging.speziLogger
import edu.stanford.spezi.core.utils.MessageNotifier
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

class HealthSummaryService @Inject constructor(
    private val healthSummaryRepository: HealthSummaryRepository,
    private val messageNotifier: MessageNotifier,
    @ApplicationContext private val context: Context,
) {
    private val logger by speziLogger()

    companion object {
        const val FILE_NAME = "health_summary.pdf"
        const val MIME_TYPE_PDF = "application/pdf"
    }

    suspend fun generateHealthSummaryPdf() {
        val pdfByteArrayResult =
            healthSummaryRepository.findHealthSummaryByUserId()
        pdfByteArrayResult.onSuccess {
            val savePdfToFile = savePdfToFile(it)
            val pdfUri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.provider",
                savePdfToFile
            )
            val intent = Intent(Intent.ACTION_VIEW)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.setDataAndType(pdfUri, MIME_TYPE_PDF)
            context.startActivity(intent)
        }.onFailure {
            messageNotifier.notify("Failed to generate Health Summary")
        }
    }

    private fun savePdfToFile(pdfBytes: ByteArray): File {
        logger.i { "PDF size: ${pdfBytes.size}" }
        val storageDir =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
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
}
