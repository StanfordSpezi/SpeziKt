package edu.stanford.spezi.module.onboarding.consent

import androidx.compose.ui.graphics.Path


data class ConsentUiState(
    val firstName: FieldState = FieldState(value = "", error = false),
    val lastName: FieldState = FieldState(value = "", error = false),
    val paths: List<Path> = emptyList(),
    val markdownText: String = """
        # Consent
        The ENGAGE-HF Android Mobile Application will connect to external devices via Bluetooth to record personal health information, including weight, heart rate, and blood pressure.
            
        Your personal information will only be shared with the research team conducting the study.
    """.trimIndent()
) {
    val isValidForm: Boolean =
        firstName.value.isNotBlank() && lastName.value.isNotBlank() && paths.isNotEmpty()
}

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