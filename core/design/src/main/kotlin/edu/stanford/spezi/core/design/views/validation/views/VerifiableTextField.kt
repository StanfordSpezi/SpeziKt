package edu.stanford.spezi.core.design.views.validation.views

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import edu.stanford.spezi.core.design.component.StringResource
import edu.stanford.spezi.core.design.views.validation.configuration.LocalValidationEngine

enum class TextFieldType {
    TEXT, SECURE
}

@Composable
fun VerifiableTextField(
    label: StringResource,
    text: MutableState<String>,
    type: TextFieldType = TextFieldType.TEXT,
    disableAutocorrection: Boolean = false,
    footer: @Composable () -> Unit = {},
) {
    VerifiableTextField(
        text,
        type,
        disableAutocorrection = disableAutocorrection,
        { Text(label.text()) },
        footer
    )
}

@Composable
fun VerifiableTextField(
    text: MutableState<String>,
    type: TextFieldType = TextFieldType.TEXT,
    disableAutocorrection: Boolean = false,
    label: @Composable () -> Unit,
    footer: @Composable () -> Unit = {},
) {
    val validationEngine = LocalValidationEngine.current

    Column {
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
                    visualTransformation = PasswordVisualTransformation()
                )
            }
        }

        Row {
            validationEngine?.let {
                ValidationResultsComposable(it.displayedValidationResults)

                Spacer(Modifier.fillMaxWidth())
            }

            footer()
        }
    }
}
