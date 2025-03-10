package edu.stanford.spezi.ui.validation

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import edu.stanford.spezi.ui.Spacings
import edu.stanford.spezi.ui.SpeziTheme
import edu.stanford.spezi.ui.StringResource
import edu.stanford.spezi.ui.ThemePreviews

enum class TextFieldType {
    TEXT, SECURE
}

@Composable
fun VerifiableTextField(
    label: StringResource,
    state: MutableState<String>,
    modifier: Modifier = Modifier,
    type: TextFieldType = TextFieldType.TEXT,
    disableAutocorrection: Boolean = false,
    footer: @Composable () -> Unit = {},
) {
    VerifiableTextField(
        label = label,
        value = state.value,
        onValueChanged = { state.value = it },
        modifier = modifier,
        type = type,
        disableAutocorrection = disableAutocorrection,
        footer = footer,
    )
}

@Composable
fun VerifiableTextField(
    label: StringResource,
    value: String,
    onValueChanged: (String) -> Unit,
    modifier: Modifier = Modifier,
    type: TextFieldType = TextFieldType.TEXT,
    disableAutocorrection: Boolean = false,
    footer: @Composable () -> Unit = {},
) {
    VerifiableTextField(
        value = value,
        onValueChanged = onValueChanged,
        modifier = modifier,
        type = type,
        disableAutocorrection = disableAutocorrection,
        footer = footer,
        label = { Text(label.text()) },
    )
}

@Composable
fun VerifiableTextField(
    state: MutableState<String>,
    modifier: Modifier = Modifier,
    type: TextFieldType = TextFieldType.TEXT,
    disableAutocorrection: Boolean = false,
    footer: @Composable () -> Unit = {},
    label: @Composable () -> Unit,
) {
    VerifiableTextField(
        value = state.value,
        onValueChanged = { state.value = it },
        modifier = modifier,
        type = type,
        disableAutocorrection = disableAutocorrection,
        footer = footer,
        label = label
    )
}

@Composable
fun VerifiableTextField(
    value: String,
    onValueChanged: (String) -> Unit,
    modifier: Modifier = Modifier,
    type: TextFieldType = TextFieldType.TEXT,
    disableAutocorrection: Boolean = false,
    footer: @Composable () -> Unit = {},
    label: @Composable () -> Unit,
) {
    val validationEngine = LocalValidationEngine.current
    val isSecure = remember(type) { type == TextFieldType.SECURE }

    // TODO: Check equality with iOS
    TextField(
        value = value,
        onValueChange = onValueChanged,
        label = label,
        keyboardActions = KeyboardActions(
            onDone = {
                validationEngine?.submit(value)
            },
        ),
        keyboardOptions = KeyboardOptions(
            keyboardType = if (isSecure) KeyboardType.Password else KeyboardType.Text,
            autoCorrect = !disableAutocorrection
        ),
        supportingText = {
            Row(Modifier.padding(vertical = Spacings.small)) {
                ValidationResultsComposable(validationEngine?.displayedValidationResults ?: emptyList())
                Spacer(Modifier.fillMaxWidth())
                footer()
            }
        },
        isError = validationEngine?.isDisplayingValidationErrors ?: true,
        visualTransformation = if (isSecure) PasswordVisualTransformation() else VisualTransformation.None,
        modifier = modifier.fillMaxWidth(),
    )
}

@ThemePreviews
@Composable
private fun VerifiableTextFieldPreview() {
    val text = remember { mutableStateOf("") }

    SpeziTheme(isPreview = true) {
        Validate(text.value, rules = listOf(ValidationRule.nonEmpty)) {
            VerifiableTextField(
                text,
                footer = { Text("Some Hint") },
            ) {
                Text("Password Text")
            }
        }
    }
}
