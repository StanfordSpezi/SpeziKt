package edu.stanford.spezi.module.onboarding.consent

interface PdfService {

    suspend fun uploadPdf(pdfBytes: ByteArray): Result<Boolean>

    suspend fun isPdfUploaded(): Result<Boolean>
}
