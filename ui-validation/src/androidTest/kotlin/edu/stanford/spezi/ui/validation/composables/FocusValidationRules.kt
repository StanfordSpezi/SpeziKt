package edu.stanford.spezi.ui.validation.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import edu.stanford.spezi.spezi.validation.Validate
import edu.stanford.spezi.spezi.validation.ValidationRule
import edu.stanford.spezi.spezi.validation.minimalPassword
import edu.stanford.spezi.spezi.validation.nonEmpty
import edu.stanford.spezi.spezi.validation.state.ReceiveValidation
import edu.stanford.spezi.spezi.validation.state.ValidationContext
import edu.stanford.spezi.spezi.validation.views.VerifiableTextField
import edu.stanford.spezi.ui.Button
import edu.stanford.spezi.ui.StringResource
import edu.stanford.spezi.ui.testing.testIdentifier

enum class Field {
    INPUT, NON_EMPTY_INPUT
}

enum class FocusValidationRulesTestIdentifier {
    EMAIL_TEXTFIELD, PASSWORD_TEXTFIELD
}

@Composable
fun FocusValidationRules() {
    val input = remember { mutableStateOf("") }
    val nonEmptyInput = remember { mutableStateOf("") }
    val validationContext = remember { mutableStateOf(ValidationContext()) }
    val lastValid = remember { mutableStateOf<Boolean?>(null) }
    val switchFocus = remember { mutableStateOf(false) }

    ReceiveValidation(validationContext) {
        Column {
            Text("Has Engines: ${if (!validationContext.value.isEmpty) "Yes" else "No"}")
            Text("Input Valid: ${if (validationContext.value.allInputValid) "Yes" else "No"}")
            lastValid.value?.let { lastValid ->
                Text("Last state: ${if (lastValid) "valid" else "invalid"}")
            }
            Button(
                onClick = {
                    val newLastValid = validationContext.value
                        .validateHierarchy(switchFocus.value)
                    lastValid.value = newLastValid
                }
            ) {
                Text("Validate")
            }
            Row {
                Text("Switch Focus")
                Switch(switchFocus.value, onCheckedChange = { switchFocus.value = it })
            }

            Validate(nonEmptyInput.value, rules = listOf(ValidationRule.nonEmpty)) {
                VerifiableTextField(
                    StringResource(Field.NON_EMPTY_INPUT.name),
                    nonEmptyInput,
                    Modifier.testIdentifier(FocusValidationRulesTestIdentifier.EMAIL_TEXTFIELD)
                )
            }

            Validate(input.value, rules = listOf(ValidationRule.minimalPassword)) {
                VerifiableTextField(
                    StringResource(Field.INPUT.name),
                    input,
                    Modifier.testIdentifier(FocusValidationRulesTestIdentifier.PASSWORD_TEXTFIELD)
                )
            }
        }
    }
}
