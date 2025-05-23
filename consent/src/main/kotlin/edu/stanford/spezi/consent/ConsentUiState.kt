package edu.stanford.spezi.consent

import androidx.compose.ui.graphics.Path
import edu.stanford.spezi.ui.ViewState
import edu.stanford.spezi.ui.markdown.MarkdownElement
import edu.stanford.spezi.ui.personalinfo.PersonNameComponents

internal data class ConsentUiState(
    val name: PersonNameComponents = PersonNameComponents(),
    val paths: List<Path> = emptyList(),
    val markdownElements: List<MarkdownElement> = emptyList(),
    val viewState: ConsentViewState = ConsentViewState.Base(ViewState.Idle),
) {
    val isValidForm: Boolean =
        (name.givenName?.isNotBlank() ?: false) && (name.familyName?.isNotBlank() ?: false) && paths.isNotEmpty()
}

internal enum class ConsentTextFieldType {
    FIRST_NAME, LAST_NAME
}

internal sealed interface ConsentAction {
    data class TextFieldUpdate(val newValue: String, val type: ConsentTextFieldType) : ConsentAction
    data class AddPath(val path: Path) : ConsentAction
    data object Undo : ConsentAction
    data class Consent(
        val documentIdentifier: String,
        val exportConfiguration: ConsentDocumentExportConfiguration,
    ) : ConsentAction
}
