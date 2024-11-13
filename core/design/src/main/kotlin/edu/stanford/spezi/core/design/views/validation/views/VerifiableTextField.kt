package edu.stanford.spezi.core.design.views.validation.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.text.input.TextFieldValue
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
    text: MutableState<String>,
    modifier: Modifier = Modifier,
    type: TextFieldType = TextFieldType.TEXT,
    disableAutocorrection: Boolean = false,
    footer: @Composable () -> Unit = {},
) {
    VerifiableTextField(
        text,
        modifier,
        type,
        disableAutocorrection = disableAutocorrection,
        footer,
        label = { Text(label.text()) },
    )
}

@Composable
fun VerifiableTextField(
    text: MutableState<String>,
    modifier: Modifier = Modifier,
    type: TextFieldType = TextFieldType.TEXT,
    disableAutocorrection: Boolean = false,
    footer: @Composable () -> Unit = {},
    label: @Composable () -> Unit,
) {
    val validationEngine = LocalValidationEngine.current

    Column(modifier) {
        // TODO: Check if this is really equivalent,
        //  since iOS specifies this as a completely separate type
        //  and there we only have this visualTransformation property
        when (type) {
            TextFieldType.TEXT -> {
                TextField(
                    text.value,
                    onValueChange = { text.value = it },
                    label = label,
                    keyboardOptions = KeyboardOptions(
                        autoCorrect = !disableAutocorrection
                    ),
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            TextFieldType.SECURE -> {
                TextField(
                    text.value,
                    onValueChange = { text.value = it },
                    label = label,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        autoCorrect = !disableAutocorrection
                    ),
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }

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
