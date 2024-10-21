package edu.stanford.spezi.module.onboarding.consent

import edu.stanford.spezi.core.design.component.StringResource

data class ConsentDocumentExportConfiguration(
    val paperSize: PaperSize = PaperSize.usLetter,
    val consentTitle: StringResource = LocalizationDefaults.exportedConsentFormTitle,
    val includingTimestamp: Boolean = true,
) {
    object LocalizationDefaults {
        val exportedConsentFormTitle = StringResource("Consent")
    }

    data class PaperSize(
        val width: Double,
        val height: Double,
    ) {
        companion object {
            private const val A4_WIDTH_IN_INCHES = 8.3
            private const val A4_HEIGHT_IN_INCHES = 11.7

            private const val US_LETTER_WIDTH_IN_INCHES = 8.5
            private const val US_LETTER_HEIGHT_IN_INCHES = 11.0

            val usLetter get() = usLetter()
            val dinA4 get() = dinA4()

            fun dinA4(pointsPerInch: Double = 72.0) = PaperSize(
                width = A4_WIDTH_IN_INCHES * pointsPerInch,
                height = A4_HEIGHT_IN_INCHES * pointsPerInch
            )

            fun usLetter(pointsPerInch: Double = 72.0) = PaperSize(
                width = US_LETTER_WIDTH_IN_INCHES * pointsPerInch,
                height = US_LETTER_HEIGHT_IN_INCHES * pointsPerInch
            )
        }
    }
}
