package edu.stanford.spezi.module.onboarding.consent

import androidx.compose.runtime.MutableState
import edu.stanford.spezi.core.design.component.StringResource

data class ConsentDocument(
    val markdown: suspend () -> ByteArray,
    val viewState: MutableState<ConsentViewState>,
    val givenNameTitle: StringResource = LocalizationDefaults.givenNameTitle,
    val givenNamePlaceholder: StringResource = LocalizationDefaults.givenNamePlaceholder,
    val familyNameTitle: StringResource = LocalizationDefaults.familyNameTitle,
    val familyNamePlaceholder: StringResource = LocalizationDefaults.familyNamePlaceholder,
    val exportConfiguration: ConsentDocumentExportConfiguration = ConsentDocumentExportConfiguration(),
) {
    object LocalizationDefaults {
        val givenNameTitle = StringResource("Given Name")
        val givenNamePlaceholder = StringResource("Given Name Placeholder")
        val familyNameTitle = StringResource("Family Name")
        val familyNamePlaceholder = StringResource("Family Name Placeholder")
    }
}