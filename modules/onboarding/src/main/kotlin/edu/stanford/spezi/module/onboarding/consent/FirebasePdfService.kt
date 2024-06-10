package edu.stanford.spezi.module.onboarding.consent

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import edu.stanford.spezi.core.coroutines.di.Dispatching
import edu.stanford.spezi.core.logging.speziLogger
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.ByteArrayInputStream
import javax.inject.Inject

internal class FirebasePdfService @Inject internal constructor(
    private val firebaseStorage: FirebaseStorage,
    private val firebaseAuth: FirebaseAuth,
    @Dispatching.IO private val ioDispatcher: CoroutineDispatcher,
) : PdfService {
    private val logger by speziLogger()

    override suspend fun uploadPdf(pdfBytes: ByteArray): Result<Boolean> = withContext(ioDispatcher) {
        runCatching {
            firebaseAuth.uid?.let { uid ->
                val inputStream = ByteArrayInputStream(pdfBytes)
                logger.i { "Uploading file to Firebase Storage" }
                firebaseStorage.getReference("users/$uid/signature.pdf")
                    .putStream(inputStream).await().task.isSuccessful
            } ?: false
        }
    }
}
