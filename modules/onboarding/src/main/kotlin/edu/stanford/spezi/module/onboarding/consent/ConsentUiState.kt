package edu.stanford.spezi.module.onboarding.consent

import androidx.compose.ui.graphics.Path


data class ConsentUiState(
    val firstName: FieldState = FieldState(value = "", error = false),
    val lastName: FieldState = FieldState(value = "", error = false),
    val paths: List<Path> = emptyList(),
    val isValidForm: Boolean = false
)

data class FieldState(
    val value: String = "",
    val error: Boolean = false
)

enum class TextFieldType {
    FIRST_NAME, LAST_NAME
}

sealed interface ConsentAction {
    data class TextFieldUpdate(val newValue: String, val type: TextFieldType) : ConsentAction
    data class AddPath(val path: Path) : ConsentAction
    data object Undo : ConsentAction
    data object Consent : ConsentAction
}