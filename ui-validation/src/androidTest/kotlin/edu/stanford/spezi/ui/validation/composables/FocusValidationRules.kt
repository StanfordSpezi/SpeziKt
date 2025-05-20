package edu.stanford.spezi.ui.validation.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import edu.stanford.spezi.ui.testIdentifier
import edu.stanford.spezi.ui.validation.OutlinedValidatedTextField
import edu.stanford.spezi.ui.validation.ReceiveValidation
import edu.stanford.spezi.ui.validation.Validate
import edu.stanford.spezi.ui.validation.ValidatedTextField
import edu.stanford.spezi.ui.validation.ValidationContext
import edu.stanford.spezi.ui.validation.ValidationRule
import edu.stanford.spezi.ui.validation.minimalPassword
import edu.stanford.spezi.ui.validation.nonEmpty

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

    ReceiveValidation(validationContext) {
        Column {
            Text("Has Engines: ${if (!validationContext.value.isEmpty) "Yes" else "No"}")
            Text("Input Valid: ${if (validationContext.value.allInputValid) "Yes" else "No"}")
            lastValid.value?.let { lastValid ->
                Text("Last state: ${if (lastValid) "valid" else "invalid"}")
            }
            Button(
                onClick = {
                    lastValid.value = validationContext.value
                        .validateHierarchy()
                }
            ) {
                Text("Validate")
            }

            Validate(nonEmptyInput.value, rules = listOf(ValidationRule.nonEmpty)) {
                ValidatedTextField(
                    value = nonEmptyInput.value,
                    onValueChange = { nonEmptyInput.value = it },
                    modifier = Modifier.testIdentifier(FocusValidationRulesTestIdentifier.EMAIL_TEXTFIELD),
                    label = {
                        Text(Field.NON_EMPTY_INPUT.name)
                    },
                )
            }

            Validate(input.value, rules = listOf(ValidationRule.minimalPassword)) {
                OutlinedValidatedTextField(
                    value = input.value,
                    onValueChange = { input.value = it },
                    modifier = Modifier.testIdentifier(FocusValidationRulesTestIdentifier.PASSWORD_TEXTFIELD),
                    label = {
                        Text(Field.INPUT.name)
                    },
                )
            }
        }
    }
}
