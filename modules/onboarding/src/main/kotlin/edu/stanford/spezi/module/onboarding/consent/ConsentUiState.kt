package edu.stanford.spezi.module.onboarding.consent

import androidx.compose.ui.graphics.Path
import edu.stanford.spezi.core.design.component.markdown.MarkdownElement
import edu.stanford.spezi.module.onboarding.views.ViewState

internal data class ConsentUiState(
    val name: PersonNameComponents = PersonNameComponents(),
    val paths: List<Path> = emptyList(),
    val markdownElements: List<MarkdownElement> = emptyList(),
    val viewState: ConsentViewState = ConsentViewState.Base(ViewState.Idle),
) {
    val isValidForm: Boolean =
        (name.givenName?.isNotBlank() ?: false) && (name.familyName?.isNotBlank() ?: false) && paths.isNotEmpty()
}

data class FieldState(
    val value: String = "",
    val error: Boolean = false,
)

enum class TextFieldType {
    FIRST_NAME, LAST_NAME
}

sealed interface ConsentAction {
    data class TextFieldUpdate(val newValue: String, val type: TextFieldType) : ConsentAction
    data class AddPath(val path: Path) : ConsentAction
    data object Undo : ConsentAction
    data class Consent(
        val documentIdentifier: String,
        val exportConfiguration: ConsentDocumentExportConfiguration
    ) : ConsentAction
}
