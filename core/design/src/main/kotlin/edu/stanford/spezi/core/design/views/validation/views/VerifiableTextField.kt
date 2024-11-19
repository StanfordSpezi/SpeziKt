package edu.stanford.spezi.core.design.views.validation.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import edu.stanford.spezi.core.design.component.StringResource
import edu.stanford.spezi.core.design.theme.SpeziTheme
import edu.stanford.spezi.core.design.theme.ThemePreviews
import edu.stanford.spezi.core.design.views.validation.Validate
import edu.stanford.spezi.core.design.views.validation.ValidationRule
import edu.stanford.spezi.core.design.views.validation.configuration.LocalValidationEngine
import edu.stanford.spezi.core.design.views.validation.nonEmpty

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
        value = state.value,
        onValueChanged = { state.value = it },
        modifier = modifier,
        type = type,
        disableAutocorrection = disableAutocorrection,
        footer = footer,
        label = { Text(label.text()) },
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

    Column(modifier) {
        // TODO: Check equality with iOS
        TextField(
            value = value,
            onValueChange = onValueChanged,
            label = label,
            keyboardOptions = KeyboardOptions(
                keyboardType = if (isSecure) KeyboardType.Password else KeyboardType.Text,
                autoCorrect = !disableAutocorrection
            ),
            visualTransformation = if (isSecure) PasswordVisualTransformation() else VisualTransformation.None,
            modifier = Modifier.fillMaxWidth(),
        )

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 6.dp)
        ) {
            ValidationResultsComposable(validationEngine?.displayedValidationResults ?: emptyList())
            footer()
        }
    }
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
