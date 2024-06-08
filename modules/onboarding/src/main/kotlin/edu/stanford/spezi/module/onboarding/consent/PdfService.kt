package edu.stanford.spezi.module.onboarding.consent

interface PdfService {

    suspend fun uploadPdf(pdfBytes: ByteArray): Boolean

}
