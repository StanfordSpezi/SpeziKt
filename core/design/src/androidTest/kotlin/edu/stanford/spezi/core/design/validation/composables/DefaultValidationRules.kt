package edu.stanford.spezi.core.design.validation.composables

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import edu.stanford.spezi.core.design.component.StringResource
import edu.stanford.spezi.core.design.validation.validation.Validate
import edu.stanford.spezi.core.design.validation.validation.ValidationRule
import edu.stanford.spezi.core.design.validation.validation.asciiLettersOnly
import edu.stanford.spezi.core.design.validation.validation.mediumPassword
import edu.stanford.spezi.core.design.validation.validation.minimalEmail
import edu.stanford.spezi.core.design.validation.validation.minimalPassword
import edu.stanford.spezi.core.design.validation.validation.nonEmpty
import edu.stanford.spezi.core.design.validation.validation.strongPassword
import edu.stanford.spezi.core.design.validation.validation.unicodeLettersOnly
import edu.stanford.spezi.core.design.validation.validation.views.VerifiableTextField

@Composable
fun DefaultValidationRules() {
    val input = remember { mutableStateOf("") }
    val rules = remember {
        listOf(
            ValidationRule.nonEmpty,
            ValidationRule.unicodeLettersOnly,
            ValidationRule.asciiLettersOnly,
            ValidationRule.minimalEmail,
            ValidationRule.minimalPassword,
            ValidationRule.mediumPassword,
            ValidationRule.strongPassword
        )
    }
    Validate(input.value, rules) {
        VerifiableTextField(
            StringResource("Field"),
            text = input
        )
    }
}
