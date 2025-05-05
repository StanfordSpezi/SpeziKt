package edu.stanford.spezi.modules.onboarding.consent

import androidx.ink.authoring.InProgressStrokeId
import androidx.ink.strokes.Stroke
import edu.stanford.spezi.ui.markdown.MarkdownElement

data class ConsentUiState(
    val firstName: FieldState = FieldState(value = "", error = false),
    val lastName: FieldState = FieldState(value = "", error = false),
    val paths: List<Pair<InProgressStrokeId, Stroke>> = emptyList(),
    val markdownElements: List<MarkdownElement> = emptyList(),
) {
    val isValidForm: Boolean =
        firstName.value.isNotBlank() && lastName.value.isNotBlank() && paths.isNotEmpty()
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
    data class AddPath(val paths: Map<InProgressStrokeId, Stroke>) : ConsentAction
    data object UndoPath : ConsentAction
    data object ClearPath : ConsentAction
    data object Consent : ConsentAction
}
