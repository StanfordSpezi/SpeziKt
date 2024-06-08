package edu.stanford.spezi.module.onboarding.consent

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import edu.stanford.spezi.core.logging.speziLogger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.ByteArrayInputStream
import javax.inject.Inject

class FirebasePdfService @Inject internal constructor(
    private val firebaseStorage: FirebaseStorage,
    private val firebaseAuth: FirebaseAuth,
) : PdfService {
    private val logger by speziLogger()

    override suspend fun uploadPdf(pdfBytes: ByteArray): Boolean {
        kotlin.runCatching {
            withContext(Dispatchers.IO) {
                firebaseAuth.uid?.let { uid ->
                    val inputStream = ByteArrayInputStream(pdfBytes)
                    logger.i { "Uploading file to Firebase Storage" }
                    val reference = firebaseStorage.getReference("users/$uid/signature.pdf")
                        .putStream(inputStream).result.metadata?.reference
                    reference?.downloadUrl?.await()
                }
            }
        }.onSuccess {
            return true
        }.onFailure {
            logger.e(it) { "Failed to upload PDF to Firebase Storage" }
            return false
        }
        return false
    }
}
