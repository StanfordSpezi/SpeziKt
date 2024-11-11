package edu.stanford.spezi.core.design.views.composables

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import edu.stanford.spezi.core.design.component.StringResource
import edu.stanford.spezi.core.design.views.validation.Validate
import edu.stanford.spezi.core.design.views.validation.ValidationRule
import edu.stanford.spezi.core.design.views.validation.asciiLettersOnly
import edu.stanford.spezi.core.design.views.validation.mediumPassword
import edu.stanford.spezi.core.design.views.validation.minimalEmail
import edu.stanford.spezi.core.design.views.validation.minimalPassword
import edu.stanford.spezi.core.design.views.validation.nonEmpty
import edu.stanford.spezi.core.design.views.validation.strongPassword
import edu.stanford.spezi.core.design.views.validation.unicodeLettersOnly
import edu.stanford.spezi.core.design.views.validation.views.VerifiableTextField

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
